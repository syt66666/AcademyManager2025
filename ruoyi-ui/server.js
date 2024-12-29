const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
const mysql = require('mysql2');  // 引入 mysql2 库
const questionnaireRouter = require('./src/api/questionnaire/submit'); // 引入其他路由文件

const app = express();

// 配置 CORS (跨域资源共享)
app.use(cors());

// 使用 bodyParser 中间件来解析请求体
app.use(bodyParser.json());

// 配置 MySQL 数据库连接池
const db = mysql.createPool({
  host: '47.99.65.198',  // 数据库主机
  user: 'dlut',       // 数据库用户名
  password: 'dlut211@',       // 数据库密码
  database: 'academymanager', // 你的数据库名称
});

// 获取用户信息的 API
app.get('/api/users/:userName', async (req, res) => {
  const { userName } = req.params;

  try {
    const [rows] = await db.promise().execute('SELECT s2.学号,s2.姓名,s2.分流形式,s2.管理部门,s2.系统内专业,s2.招生录取专业 FROM sys_user s1 join student s2 on s1.user_name=s2.学号 WHERE user_name = ?', [userName]);

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

// 注册其他路由
app.use('/questionnaire', questionnaireRouter);  // 所有 /questionnaire 路径的请求将交给 questionnaireRouter 处理

// 启动服务器
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`后端服务器已启动，端口：${PORT}`);
});
