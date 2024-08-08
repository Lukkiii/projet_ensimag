const express = require('express')
const router = express.Router()
const groups = require('../controllers/groups.js')

router.get('/api/mygroups', groups.getMyGroups)
router.get('/api/mygroups/:gid', groups.getMyGroupMembersById)
router.get('/api/groupsmember', groups.getAllGroupsJoined)

router.post('/api/mygroups', groups.createGroup)

router.put('/api/mygroups/:gid/:uid', groups.addMemberToGroup)

router.delete('/api/mygroups/:gid', groups.deleteGroup)
router.delete('/api/mygroups/:gid/:uid', groups.removeMemberFromGroup)

module.exports = router
