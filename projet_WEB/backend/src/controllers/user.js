const status = require('http-status')
const userModel = require('../models/users.js')
const has = require('has-keys')
const CodeError = require('../util/CodeError.js')
const bcrypt = require('bcrypt')
const jws = require('jws')
require('mandatoryenv').load(['TOKENSECRET'])
const { TOKENSECRET } = process.env

function validPassword (password) {
  return /^(?=.*[\d])(?=.*[A-Z])(?=.*[a-z])(?=.*[!@#$%^&*])[\w!@#$%^&*]{8,}$/.test(password)
}

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

async function login(req, res) {
  // #swagger.tags = ['Users']
  // #swagger.summary = 'Verify credentials of user using email and password and return token'
  // #swagger.parameters['obj'] = { in: 'body', schema: { $email: 'John.Doe@acme.com', $password: '12345'}}
  if (!has(req.body, ['email', 'password'])) throw new CodeError('You must specify the email and password', status.BAD_REQUEST)
  const { email, password } = req.body
  const user = await getUserFromEmail(email)
  if (await bcrypt.compare(password, user.passhash)) {
    const token = jws.sign({ header: { alg: 'HS256' }, payload: email, secret: TOKENSECRET })
    res.json({ status: true, message: 'Login/Password ok', token, user: user })
    return
  }
  res.status(status.FORBIDDEN).json({ status: false, message: 'Wrong password!' })
}

async function newUser (req, res) {
  // #swagger.tags = ['Users']
  // #swagger.summary = 'Add a new user'
  // #swagger.parameters['obj'] = { in: 'body', schema: { $name: 'John Doe', $email: 'John.Doe@acme.com', $password: '1m02P@SsF0rt!'}}
  if (!has(req.body, ['name', 'email', 'password'])) throw new CodeError('You must specify the name and email', status.BAD_REQUEST)
  // add comment
  const { name, email, password } = req.body
  if (!validPassword(password)) throw new CodeError('Weak password!', status.BAD_REQUEST)
  await userModel.create({ name, email, passhash: await bcrypt.hash(password, 2), isAdmin: false})
  res.status(status.CREATED).json({ status: true, message: 'User Added' })
}

async function getUsers (req, res) {
  // TODO : verify if the token is valid...
  // #swagger.tags = ['Users']
  // #swagger.summary = 'Get all users if the user is a valid user'
  verifyTokenPresent(req, res);
  await getUserFromEmail(req.loginEmail)
  const data = await userModel.findAll({ attributes: ['id', 'name', 'email', 'isAdmin'] })
  res.json({ status: true, message: 'Returning users', data })
}

async function updateUser (req, res) {
  // TODO : verify if the token is valid and correspond to an admin
  // #swagger.tags = ['Users']
  // #swagger.summary = 'Update user details if the login user is an admin'
  // #swagger.parameters['obj'] = { in: 'body', schema: { $name: 'John Doe', $email: 'John.Doe@acme.com', $password: '1m02P@SsF0rt!' }}
  
  // verify if the token is valid and correspond to an admin
  verifyTokenPresent(req, res);
  const user = await getUserFromEmail(req.loginEmail)
  if (!user.isAdmin) {
    res.status(status.FORBIDDEN).json({ status: false, message: 'Only admin can modify user details!' })
    return
  }
  
  // update the user with the id
  const userModified = {}
  for (const field of ['name', 'email', 'password']) {
    if (has(req.body, field)) {
      if (field === 'password') {
        userModified.passhash = await bcrypt.hash(req.body.password, 2)
      } else {
        userModified[field] = req.body[field]
      }
    }
  }
  if (Object.keys(userModified).length === 0) throw new CodeError('You must specify the name, email or password', status.BAD_REQUEST)
  await userModel.update(userModified, { where: { id: req.params.id } })
  res.json({ status: true, message: 'User updated' })
}

async function deleteUser (req, res) {
  // TODO : verify if the token is valid and correspond to an admin
  // #swagger.tags = ['Users']
  // #swagger.summary = 'Delete user if the login user is an admin'

  if (!has(req.params, 'id')) throw new CodeError('You must specify the id', status.BAD_REQUEST)

  // verify if the token is valid and correspond to an admin
  verifyTokenPresent(req, res);
  const user = await getUserFromEmail(req.loginEmail)
  if (!user.isAdmin) {
    res.status(status.FORBIDDEN).json({ status: false, message: 'Only admin can delete user!' })
    return
  }

  // delete the user with the id
  const { id } = req.params
  await userModel.destroy({ where: { id } })
  res.json({ status: true, message: 'User deleted' })
}

async function updatePassword (req, res) {
  // #swagger.tags = ['Users']
  // #swagger.summary = 'Update password'
  // #swagger.parameters['obj'] = { in: 'body', schema: { $password: '1m02P@SsF0rt!'}}
  verifyTokenPresent(req, res);
  if (!has(req.body, ['password'])) throw new CodeError('You must specify the password', status.BAD_REQUEST)
  if (!validPassword(req.body.password)) throw new CodeError('Weak password!', status.BAD_REQUEST)
  await getUserFromEmail(req.loginEmail)
  await userModel.update({ passhash: await bcrypt.hash(req.body.password, 2) }, { where: { email: req.loginEmail } })
  res.json({ status: true, message: 'Password updated' })
}


module.exports = {
  login,
  newUser, 
  getUsers, 
  updateUser, 
  deleteUser, 
  updatePassword  
}
