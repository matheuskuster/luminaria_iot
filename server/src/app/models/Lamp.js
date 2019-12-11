import Sequelize, { Model } from 'sequelize';

class Lamp extends Model {
  static init(sequelize) {
    super.init(
      {
        code: Sequelize.STRING,
        nickname: Sequelize.STRING,
      },
      { sequelize }
    );

    this.addHook('beforeCreate', lamp => {
      lamp.code =
        Math.floor(100000 + Math.random() * 900000).toString() +
        new Date().getDate().toString();
    });

    return this;
  }

  static associate(models) {
    this.belongsTo(models.User, { foreignKey: 'user', as: 'owner' });
  }
}

export default Lamp;
