import * as Yup from 'yup';
import User from '../models/User';
import File from '../models/File';

class UserController {
  async index(req, res) {
    const user = await User.findByPk(req.userId, {
      attributes: ['login', 'email', 'cellphone', 'avatar_id'],
      include: [
        {
          model: File,
          as: 'avatar',
          attributes: ['path', 'url'],
        },
      ],
    });

    return res.status(200).json(user);
  }

  async store(req, res) {
    const schema = Yup.object().shape({
      cellphone: Yup.string().required(),
      login: Yup.string().required(),
      email: Yup.string()
        .email()
        .required(),
      password: Yup.string()
        .required()
        .min(6),
    });

    if (!(await schema.isValid(req.body)))
      return res.status(400).json({ error: 'Validation failed' });

    const userExists = await User.findOne({ where: { email: req.body.email } });

    if (userExists)
      return res.status(400).json({ error: 'User already exists.' });

    const { id, name, email, cellphone } = await User.create(req.body);

    return res.json({ id, name, email, cellphone });
  }
}

export default new UserController();
