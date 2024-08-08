const Sequelize = require('sequelize')
const db = require('./database.js')

// table of all groups
const groups = db.define('groups', {
  id: {
    primaryKey: true,
    type: Sequelize.INTEGER,
    autoIncrement: true
  },
  name: {
    type: Sequelize.STRING(128),
    unique: true
  },
  ownerId: {
    type: Sequelize.INTEGER,
    reference: {
      model: 'users',
      key: 'id',
      onDelete: 'CASCADE',
      onUpdate: 'CASCADE'
    },
  }
}, { timestamps: false })

// table of all group members
const usersGroups = db.define('usersGroups', {
    memberId: {
      type: Sequelize.INTEGER,
      primaryKey: true,
      allowNull: false,
      reference: {
        model: 'users',
        key: 'id',
        onDelete: 'CASCADE',
        onUpdate: 'CASCADE'
      },
    },
    groupId: {
      type: Sequelize.INTEGER,
      primaryKey: true,
      allowNull: false,
      reference: {
        model: 'groups',
        key: 'id',
        onDelete: 'CASCADE',
        onUpdate: 'CASCADE'
      },
    }
}, { timestamps: false })



module.exports = {
    groups,
    usersGroups
}