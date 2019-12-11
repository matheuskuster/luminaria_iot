module.exports = {
  up: (queryInterface, Sequelize) => {
    return queryInterface.addColumn('lamps', 'nickname', {
      type: Sequelize.STRING,
      allowNull: true,
    });
  },

  down: queryInterface => queryInterface.removeColumn('lamps', 'nickname'),
};
