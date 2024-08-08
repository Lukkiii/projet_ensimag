const status = require('http-status')
const userModel = require('../models/users.js')
const {groups: groupModel, usersGroups: usersGroupModel} = require('../models/groups.js')
const has = require('has-keys')
const CodeError = require('../util/CodeError.js')
const jws = require('jws')
require('mandatoryenv').load(['TOKENSECRET'])
const { TOKENSECRET } = process.env

function verifyTokenPresent(req, res) {
    // check if the token is present and valid, else throw an error
    if (!req.headers || !req.headers.hasOwnProperty('x-access-token')) {
      throw {code: 403, message: 'Token missing'}
    }
    if (!jws.verify(req.headers['x-access-token'], 'HS256', TOKENSECRET)) {
      throw {code: 403, message: 'Token invalid'}
    }
    req.loginEmail = jws.decode(req.headers['x-access-token']).payload;
}
  
  // get user from email, throw an error if not found
  async function getUserFromEmail(email) {
    const user = await userModel.findOne({ where: { email } });
  
    // throw an error if user not found
    if (!user) throw new CodeError('User not found', status.NOT_FOUND)
  
    return user;
}

async function getMyGroups(req, res) {
    // #swagger.tags = ['Groups']
    // #swagger.summary = 'Get all groups managed by the logged in user'
    verifyTokenPresent(req, res)
    const user = await getUserFromEmail(req.loginEmail)
    if (user.isAdmin) {
        const groups = await groupModel.findAll()
        return res.json({status: true, message: "All groups in the system", groups})
    }
    const groups = await groupModel.findAll({ where: { ownerId: user.id } })
    res.json({status: true, message: "All groups managed by login user", groups})
}

async function getMyGroupMembersById(req, res) {
    // #swagger.tags = ['Groups']
    // #swagger.summary = 'Get all members of a group managed by the logged in user'
    verifyTokenPresent(req, res)
    const user = await getUserFromEmail(req.loginEmail)

    // check if the group exists 
    const group = await groupModel.findOne({ where: { id: req.params.gid } })
    if (!group) throw new CodeError('Group not found', status.NOT_FOUND)

    // check if the user is the owner of the group, member of the group or admin 
    const member = await usersGroupModel.findOne({ where: { memberId: user.id, groupId: req.params.gid } })
    if (group.ownerId != user.id && member == null && user.isAdmin == false) {
        throw new CodeError('User does not have the rights, only group owner, group member and owner can perform this action', status.FORBIDDEN)
    }

    // get all members of the group
    const members = await usersGroupModel.findAll({ where: { groupId: req.params.gid } })
    
    // map them into a list of memberIds
    const memberIds = members.map(member => member.memberId)
    res.json({status: true, message: "All members of the group", memberIds})
}

async function getAllGroupsJoined(req, res) {
    // #swagger.tags = ['Groups']
    // #swagger.summary = 'Get all groups joined by the logged in user'
    verifyTokenPresent(req, res)
    const user = await getUserFromEmail(req.loginEmail)
    const groups = await usersGroupModel.findAll({ where: { memberId: user.id } })

    // map them into a list of group names and id
    const groupsNameId = await Promise.all(groups.map(async group => {
        const groupDetails = await groupModel.findOne({ where: { id: group.groupId } })
        return {name: groupDetails.name, id: groupDetails.id}
    }))
    res.json({status: true, message: "All groups joined by login user", groupsNameId})
}

async function createGroup(req, res) {
    // #swagger.tags = ['Groups']
    // #swagger.summary = 'Create a new group'
    // #swagger.parameters['obj'] = { in: 'body', schema: { $name: 'My Group'}}
    verifyTokenPresent(req, res)
    const user = await getUserFromEmail(req.loginEmail)
    if (!has(req.body, ['name'])) throw new CodeError('You must specify the name of the group', status.BAD_REQUEST)

    // check if name exists
    const groupExists = await groupModel.findOne({ where: { name: req.body.name } })
    if (groupExists) throw new CodeError('Group name already exists', status.CONFLICT)

    // create the group
    const group = await groupModel.create({ name: req.body.name, ownerId: user.id })
    res.status(status.CREATED).json({status: true, message: "Group created", group})
}

async function addMemberToGroup(req, res) {
    // #swagger.tags = ['Groups']
    // #swagger.summary = 'Add a member to a group'
    verifyTokenPresent(req, res)
    const user = await getUserFromEmail(req.loginEmail)

    // check if the group exists and the user is the owner
    const group = await groupModel.findOne({ where: { id: req.params.gid, ownerId: user.id } })
    if (!group) throw new CodeError('Group not found', status.NOT_FOUND)

    // check if the member to be added exists
    const member = await userModel.findOne({ where: { id: req.params.uid } })
    if (!member) throw new CodeError('User not found', status.NOT_FOUND)

    // check if the member is already in the group
    const memberExists = await usersGroupModel.findOne({ where: { memberId: req.params.uid, groupId: req.params.gid } })
    if (memberExists) throw new CodeError('User already in group', status.CONFLICT)

    // add the member to the group
    await usersGroupModel.create({ memberId: req.params.uid, groupId: req.params.gid })
    res.json({status: true, message: "Member added to group"})
}

async function removeMemberFromGroup(req, res) {
    // #swagger.tags = ['Groups']
    // #swagger.summary = 'Remove a member from a group'
    verifyTokenPresent(req, res)
    const user = await getUserFromEmail(req.loginEmail)

    // check if the group exists
    const group = await groupModel.findOne({ where: { id: req.params.gid } })
    if (!group) throw new CodeError('Group not found', status.NOT_FOUND)

    // check if the user is an admin, owner of the group, or the member to be removed 
    if (user.isAdmin == false && group.ownerId != user.id && user.id != req.params.uid) {
        throw new CodeError('User does not have the rights, only group owner, admin and the member himself can perform this action', status.CONFLICT)
    } 

    // check if the member is in the group
    const memberExists = await usersGroupModel.findOne({ where: { memberId: req.params.uid, groupId: req.params.gid } })
    if (!memberExists) throw new CodeError('User not in group', status.NOT_FOUND)

    // remove the member from the group
    await usersGroupModel.destroy({ where: { memberId: req.params.uid, groupId: req.params.gid } })
    res.json({status: true, message: "Member removed from group"})
}

async function deleteGroup(req, res) {
    // #swagger.tags = ['Groups']
    // #swagger.summary = 'Delete a group'
    verifyTokenPresent(req, res)
    const user = await getUserFromEmail(req.loginEmail)

    // check if the group exists
    const group = await groupModel.findOne({ where: { id: req.params.gid } })
    if (!group) throw new CodeError('Group not found', status.NOT_FOUND)

    // check if the user is an admin or owner of the group
    if (user.isAdmin == false && group.ownerId != user.id) {
        throw new CodeError('User does not have the rights, only group owner and admin can perform this action', status.CONFLICT)
    }

    // delete the group
    await groupModel.destroy({ where: { id: req.params.gid } })
    await usersGroupModel.destroy({ where: { groupId: req.params.gid } })

    res.json({status: true, message: "Group deleted"})
}



module.exports = {
    getMyGroups,
    getMyGroupMembersById,
    getAllGroupsJoined,
    createGroup,
    addMemberToGroup,
    removeMemberFromGroup,
    deleteGroup
}
