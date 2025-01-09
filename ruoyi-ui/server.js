const express = require('express');
const cors = require('cors');
const mysql = require('mysql2');

const questionnaireRouter = require('./src/api/questionnaire/submit');

const app = express();

// 配置 CORS
app.use(cors());

// 使用内置中间件解析 JSON
app.use(express.json());

//todo: fix the api
// 配置 MySQL 数据库连接池
const db = mysql.createPool({
  host: '47.99.65.198',
  user: 'dlut',
  password: 'dlut211@',
  database: 'academymanager',
});

// 获取用户信息的 API
app.get('/api/users/:userName', async (req, res) => {
  const { userName } = req.params;

  try {
    const [rows] = await db.promise().execute('SELECT s2.studentId,s2.studentName,s2.academy,s2.divertForm,s2.systemMajor,s2.major,s2.InnovationClass FROM sys_user s1 join student s2 on s1.user_name=s2.studentId WHERE user_name = ?', [userName]);

    if (rows.length === 0) {
      return res.status(404).json({ message: '用户未找到' });
    }

    res.json(rows[0]);
  } catch (err) {
    console.error('查询失败:', err);
    return res.status(500).json({ message: '服务器内部错误' });
  }
});
// 检查用户是否已经回答过问卷
app.get('/api/check-questionnaire-completed', async (req, res) => {
  const { userName, questionnaireId } = req.query;

  try {
    const [rows] = await db.promise().execute(
      'SELECT * FROM user_questionnaire_answers WHERE user_name = ? AND questionnaire_id = ?',
      [userName, questionnaireId]
    );

    if (rows.length > 0) {
      return res.json({ completed: true });
    } else {
      return res.json({ completed: false });
    }
  } catch (error) {
    console.error('查询失败:', error);
    return res.status(500).json({ error: '服务器内部错误' });
  }
});


// 提交问卷答案的 API
app.post('/updateStudent', async (req, res) => {
  const { studentId, afterMajor, afterAcademy,change_major_type } = req.body;

  if (!studentId || !afterMajor || !afterAcademy||!change_major_type) {
    return res.status(400).json({ message: '缺少必要的参数' });
  }

  try {
    const [result] = await db.promise().query('UPDATE student SET afterMajor = ?, afterAcademy = ? ,change_major_type= ? WHERE studentId = ?', [afterMajor, afterAcademy,change_major_type, studentId]);

    if (result.affectedRows === 0) {
      return res.status(404).json({ message: '未找到对应的学生ID' });
    }

    res.status(200).json({ message: '更新成功' });
  } catch (err) {
    console.error('数据库更新失败:', err);
    return res.status(500).json({ message: '更新失败' });
  }
});

// 注册其他路由
// app.use('/questionnaire', questionnaireRouter);

// 提交问卷答案的 API
app.post('/submit', (req, res) => {
  const { user_name, questionnaire_id, change_adress,change_major,after_change_major,after_change_adress,change_major_type } = req.body;

  // 日志输出请求体数据
  console.log('收到的请求数据:', req.body);

  // 检查请求体中的必要字段
  if (!user_name || !questionnaire_id || ! change_adress||!change_major||!after_change_major||!after_change_adress||!change_major_type) {
    console.error('缺少必要的参数:', { user_name,questionnaire_id, change_adress,change_major,after_change_adress,after_change_major,change_major_type }); // 如果缺少参数，记录详细信息
    return res.status(400).json({ message: '缺少必要的参数' });
  }

  // 插入用户的答案到数据库
  const query = 'INSERT INTO user_questionnaire_answers (user_name,questionnaire_id, change_adress,change_major,after_change_adress,after_change_major,change_major_type) VALUES (?, ?, ?,?,?,?,?)';
  console.log('准备插入的SQL查询:', query); // 记录SQL查询

  db.query(query, [user_name, questionnaire_id, change_adress,change_major,after_change_major,after_change_adress,change_major_type], (err, result) => {
    if (err) {
      console.error('数据库插入失败:', err); // 记录数据库插入错误
      return res.status(500).json({ message: '提交失败' });
    }

    console.log('插入成功，返回的结果:', result); // 记录成功插入后的结果
    res.status(200).json({ message: '提交成功' });
  });
});

// 启动服务器
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`后端服务器已启动，端口：${PORT}`);
});
