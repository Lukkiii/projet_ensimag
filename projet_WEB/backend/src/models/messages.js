const Sequelize = require('sequelize')
const db = require('./database.js')

// table of all messages in a group
const messagesGroups = db.define('messagesGroups', {
    id: {
      primaryKey: true,
      type: Sequelize.INTEGER,
      autoIncrement: true
    },
    content: {
      type: Sequelize.STRING(128)
    },
    createdAt: {
        type: Sequelize.DATE,
        defaultValue: Sequelize.NOW
    },
    userId: {
      type: Sequelize.INTEGER,
      reference: {
        model: 'users',
        key: 'id',
        onDelete: 'CASCADE',
        onUpdate: 'CASCADE'
      },
    },
    groupId: {
      type: Sequelize.INTEGER,
      reference: {
        model: 'groups',
        key: 'id',
        onDelete: 'CASCADE',
        onUpdate: 'CASCADE'
      },
    },
}, { timestamps: false })

module.exports = messagesGroups