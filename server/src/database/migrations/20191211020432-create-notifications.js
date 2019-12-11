module.exports = {
  up: (queryInterface, Sequelize) => {
    return queryInterface.createTable('notifications', {
      id: {
        type: Sequelize.INTEGER,
        allowNull: false,
        autoIncrement: true,
        primaryKey: true,
      },
      text: {
        type: Sequelize.STRING,
        allowNull: false,
      },
      to_accept: {
        type: Sequelize.BOOLEAN,
        defaultValue: false,
      },
      accepted: {
        type: Sequelize.BOOLEAN,
        defaultValue: false,
      },
      user_id: {
        type: Sequelize.INTEGER,
        allowNull: false,
        references: { model: 'users', key: 'id' },
        onUpdate: 'CASCADE',
        onDelete: 'CASCADE',
      },
      created_at: {
        type: Sequelize.DATE,
        allowNull: false,
      },
      updated_at: {
        type: Sequelize.DATE,
        allowNull: false,
      },
    });
  },

  down: queryInterface => queryInterface.dropTable('notifications'),
};
