import Sequelize, { Model } from 'sequelize';

class Relation extends Model {
  static init(sequelize) {
    super.init(
      {
        ask_user: Sequelize.INTEGER,
        accept_user: Sequelize.INTEGER,
        accepted: Sequelize.BOOLEAN,
      },
      { sequelize }
    );

    return this;
  }

  static associate(models) {
    this.belongsTo(models.User, { foreignKey: 'ask_user', as: 'request' });
    this.belongsTo(models.User, {
      foreignKey: 'accept_user',
      as: 'requested',
    });
  }
}

export default Relation;
