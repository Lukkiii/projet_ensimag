const userModel = require('../models/users.js')
const {groups: groupModel, usersGroups: userGroupModel} = require('../models/groups.js')
const messagesGroupModel = require('../models/messages.js')
const bcrypt = require('bcrypt');
// Ajouter ici les nouveaux require des nouveaux modèles

// eslint-disable-next-line no-unexpected-multiline
(async () => {
  // Regénère la base de données
  await require('../models/database.js').sync({ force: true })
  console.log('Base de données créée.')
  // Initialise la base avec quelques données
  const passhash = await bcrypt.hash('123456', 2)
  console.log(passhash)
  await userModel.create({
    name: 'Sebastien Viardot', email: 'Sebastien.Viardot@grenoble-inp.fr', passhash
  })
  await userModel.create({
    name: 'superuser', email: 'superuser@gmail.com', passhash: await bcrypt.hash('superuser', 2), isAdmin: true
  })
  // Ajouter ici le code permettant d'initialiser par défaut la base de donnée
  await userModel.create({
    name: 'lukii', email: 'lukii@gmail.com', passhash: await bcrypt.hash('123456', 2), isAdmin: false
  })
  await userModel.create({
    name: 'thory', email: 'thory@gmail.com', passhash: await bcrypt.hash('123456', 2), isAdmin: false
  })

  await userModel.create({
    name: 'luke', email: 'luke@gmail.com', passhash: await bcrypt.hash('123456', 2), isAdmin: false
  })

  await userModel.create({
    name: 'estella', email: 'estella@gmail.com', passhash: await bcrypt.hash('123456', 2), isAdmin: false
  })
  
  await groupModel.create({
    name: 'Groupe 1', ownerId: 4
  })
  await groupModel.create({
    name: 'Groupe 2', ownerId: 3
  })
  await groupModel.create({
    name: 'Groupe 3', ownerId: 4
  })

  await userGroupModel.create({
    memberId: 4, groupId: 1
  })
  await userGroupModel.create({
    memberId: 3, groupId: 1
  })
  await userGroupModel.create({
    memberId: 4, groupId: 2
  })
  await userGroupModel.create({
    memberId: 3, groupId: 2
  })

  await messagesGroupModel.create({
    content: 'Bonjour, ca va?', userId: 3, groupId: 1
  })
  await messagesGroupModel.create({
    content: 'Oui et toi?', userId: 4, groupId: 1
  })
  await messagesGroupModel.create({
    content: 'Oui, merci', userId: 3, groupId: 1
  })
  await messagesGroupModel.create({
    content: 'Bonjour, ca va?', userId: 4, groupId: 2
  })
  await messagesGroupModel.create({
    content: 'Alors, pas du tout.', userId: 3, groupId: 2
  })
  await messagesGroupModel.create({
    content: 'Je suis désolé', userId: 4, groupId: 2
  })

  
})()
