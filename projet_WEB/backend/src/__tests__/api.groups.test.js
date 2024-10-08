const app = require('../app')
const request = require('supertest')

test ('Test if user can login and list all his groups', async () => {
    let response = await request(app)
        .post('/login')
        .send({ email: 'thory@gmail.com', password: '123456' })
    expect(response.statusCode).toBe(200)
    expect(response.body).toHaveProperty('token')
    response = await request(app)
        .get('/api/mygroups')
        .set('x-access-token', response.body.token)
    expect(response.statusCode).toBe(200)
    expect(response.body.message).toBe('All groups managed by login user')
    expect(response.body.groups.length).toBeGreaterThan(0)
})

test ('Test if user can create a group', async () => {
    let response = await request(app)
        .post('/login')
        .send({ email: 'thory@gmail.com', password: '123456' })
    expect(response.statusCode).toBe(200)
    expect(response.body).toHaveProperty('token')
    response = await request(app)
        .post('/api/mygroups')
        .set('x-access-token', response.body.token)
        .send({ name: 'Ensimag' })
    expect(response.statusCode).toBe(201)
})

test ('Test if user can list all members of a group', async () => {
    let response = await request(app)
        .post('/login')
        .send({ email: 'thory@gmail.com', password: '123456' })
    expect(response.statusCode).toBe(200)
    expect(response.body).toHaveProperty('token')
    response = await request(app)
        .get('/api/mygroups/1')
        .set('x-access-token', response.body.token)
    expect(response.statusCode).toBe(200)
})

test ('Test if user can list all groups joined', async () => {
    let response = await request(app)
        .post('/login')
        .send({ email: 'thory@gmail.com', password: '123456' })
    expect(response.statusCode).toBe(200)
    expect(response.body).toHaveProperty('token')
    response = await request(app)
        .get('/api/groupsmember')
        .set('x-access-token', response.body.token)
    expect(response.statusCode).toBe(200)
    expect(response.body.message).toBe('All groups joined by login user')
})

test ('Test if owner can add a user in the group', async () => {
    let response = await request(app)
        .post('/login')
        .send({ email: 'thory@gmail.com', password: '123456' })
    expect(response.statusCode).toBe(200)
    expect(response.body).toHaveProperty('token')
    response = await request(app)
        .put('/api/mygroups/1/1')
        .set('x-access-token', response.body.token)
    expect(response.statusCode).toBe(200)
})

test ("Test if user can't add a user in the group", async () => {
    let response = await request(app)
        .post('/login')
        .send({ email: 'luke@gmail.com', password: '123456' })
    expect(response.statusCode).toBe(200)
    expect(response.body).toHaveProperty('token')
    response = await request(app)
        .put('/api/mygroups/1/1')
        .set('x-access-token', response.body.token)
    expect(response.statusCode).toBe(404)
})

test ('Test if owner can remove a user from the group', async () => {
    let response = await request(app)
        .post('/login')
        .send({ email: 'thory@gmail.com', password: '123456' })
    expect(response.statusCode).toBe(200)
    expect(response.body).toHaveProperty('token')
    response = await request(app)
        .delete('/api/mygroups/1/1')
        .set('x-access-token', response.body.token)
    expect(response.statusCode).toBe(200)
})

test ("Test if user can't remove a user from the group", async () => {
    let response = await request(app)
        .post('/login')
        .send({ email: 'luke@gmail.com', password: '123456' })
    expect(response.statusCode).toBe(200)
    expect(response.body).toHaveProperty('token')
    response = await request(app)
        .delete('/api/mygroups/1/1')
        .set('x-access-token', response.body.token)
    expect(response.statusCode).toBe(409)
})

test ('Test if admin can delete a group', async () => {
    let response = await request(app)
        .post('/login')
        .send({ email: 'superuser@gmail.com', password: 'superuser' })
    expect(response.statusCode).toBe(200)
    expect(response.body).toHaveProperty('token')
    response = await request(app)
        .delete('/api/mygroups/2')
        .set('x-access-token', response.body.token)
    expect(response.statusCode).toBe(200)
})

test ('Test if owner can delete a group', async () => {
    let response = await request(app)
        .post('/login')
        .send({ email: 'thory@gmail.com', password: '123456' })
    expect(response.statusCode).toBe(200)
    expect(response.body).toHaveProperty('token')
    response = await request(app)
        .delete('/api/mygroups/3')
        .set('x-access-token', response.body.token)
    expect(response.statusCode).toBe(200)
})

test ("Test if user can't delete a group", async () => {
    let response = await request(app)
        .post('/login')
        .send({ email: 'luke@gmail.com', password: '123456' })
    expect(response.statusCode).toBe(200)
    expect(response.body).toHaveProperty('token')
    response = await request(app)
        .delete('/api/mygroups/1')
        .set('x-access-token', response.body.token)
    expect(response.statusCode).toBe(409)
})
