const status = require('http-status')
const {groups: groupModel, usersGroups: usersGroupModel} = require('../models/groups.js')
const userModel = require('../models/users.js')
const messagesGroupModel = require('../models/messages.js')
const jws = require('jws')
const has = require('has-keys')
const CodeError = require('../util/CodeError.js')
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

async function getMessages(req, res) {
    // #swagger.tags = ['Messages']
    // #swagger.summary = 'Get all messages in a group'
    verifyTokenPresent(req, res)
    const user = await getUserFromEmail(req.loginEmail)

    // check if the group exists
    const group = await groupModel.findOne({ where: { id: req.params.gid } })
    if (!group) throw new CodeError('Group not found', status.NOT_FOUND)
    // check if the user is in the group or user is the owner
    const member = await usersGroupModel.findOne({ where: { memberId: user.id, groupId: req.params.gid } })
    if (!member && group.ownerId !== user.id) throw new CodeError('User not in group', status.FORBIDDEN)

    // get all messages in the group
    const messages = await messagesGroupModel.findAll({ where: { groupId: req.params.gid } })
    res.json({status: true, message: "All messages in group", messages})
}

async function postMessage(req, res) {
    // #swagger.tags = ['Messages']
    // #swagger.summary = 'Post a message to a group'
    verifyTokenPresent(req, res)
    const user = await getUserFromEmail(req.loginEmail)

    // check if the group exists
    const group = await groupModel.findOne({ where: { id: req.params.gid } })
    if (!group) throw new CodeError('Group not found', status.NOT_FOUND)

    // check if the user is in the group or user is the owner
    const member = await usersGroupModel.findOne({ where: { memberId: user.id, groupId: req.params.gid } })
    if (!member && group.ownerId !== user.id) throw new CodeError('User not in group', status.FORBIDDEN)

    // check if the message is present
    if (!has(req.body, ['content'])) throw new CodeError('You must specify the message', status.BAD_REQUEST)

    // post the message
    await messagesGroupModel.create({ userId: user.id, groupId: req.params.gid, content: req.body.content })
    res.status(status.CREATED).json({status: true, message: "Message posted"})
}

module.exports = {
    getMessages,
    postMessage
}