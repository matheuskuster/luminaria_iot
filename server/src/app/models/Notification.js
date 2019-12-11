import Sequelize, { Model } from 'sequelize';

class Notification extends Model {
  static init(sequelize) {
    super.init(
      {
        text: Sequelize.STRING,
        to_accept: Sequelize.BOOLEAN,
        accepted: Sequelize.BOOLEAN,
      },
      { sequelize }
    );

    return this;
  }

  static associate(models) {
    this.belongsTo(models.User, { foreignKey: 'user_id', as: 'user' });
  }
}

export default Notification;
