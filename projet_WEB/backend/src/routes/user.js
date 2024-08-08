const express = require('express')
const router = express.Router()
const user = require('../controllers/user.js')

router.get('/api/users', user.getUsers)

router.post('/login', user.login)
router.post('/register', user.newUser)

router.put('/api/users/:id', user.updateUser)
router.put('/api/password', user.updatePassword)

router.delete('/api/users/:id', user.deleteUser)

module.exports = router
