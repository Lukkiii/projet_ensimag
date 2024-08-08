const express = require('express')
const router = express.Router()
const message = require('../controllers/message.js')

router.get('/api/messages/:gid', message.getMessages)

router.post('/api/messages/:gid', message.postMessage)


module.exports = router