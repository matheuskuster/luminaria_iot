module.exports = {
  dialect: 'postgres',
  host: 'localhost',
  username: 'postgres',
  password: 'docker',
  database: 'lampada',
  define: {
    timestamps: true,
    underscored: true,
    underscoredAll: true,
  },
};
