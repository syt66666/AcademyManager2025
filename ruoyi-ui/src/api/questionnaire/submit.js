const express = require('express');
const router = express.Router();
const mysql = require('mysql2');

// 配置数据库连接
const db = mysql.createConnection({
  host: '47.99.65.198',
  user: 'dlut',
  password: 'dlut211@',
  database: 'academymanager'
});

// 连接数据库
db.connect((err) => {
  if (err) {
    console.error('数据库连接失败:', err); // 记录连接失败
    return;
  }
  console.log('数据库连接成功');
});


// // 提交问卷答案的 API
// router.post('/submit', (req, res) => {
//   const { user_name, questionnaire_id, change_adress,change_major,after_change_major,after_change_adress,change_major_type } = req.body;
//
//   // 日志输出请求体数据
//   console.log('收到的请求数据:', req.body);
//
//   // 检查请求体中的必要字段
//   if (!user_name || !questionnaire_id || ! change_adress||!change_major||!after_change_major||!after_change_adress||!change_major_type) {
//     console.error('缺少必要的参数:', { user_name,questionnaire_id, change_adress,change_major,after_change_adress,after_change_major,change_major_type }); // 如果缺少参数，记录详细信息
//     return res.status(400).json({ message: '缺少必要的参数' });
//   }
//
//   // 插入用户的答案到数据库
//   const query = 'INSERT INTO user_questionnaire_answers (user_name,questionnaire_id, change_adress,change_major,after_change_adress,after_change_major,change_major_type) VALUES (?, ?, ?,?,?,?,?)';
//   console.log('准备插入的SQL查询:', query); // 记录SQL查询
//
//   db.query(query, [user_name, questionnaire_id, change_adress,change_major,after_change_major,after_change_adress,change_major_type], (err, result) => {
//     if (err) {
//       console.error('数据库插入失败:', err); // 记录数据库插入错误
//       return res.status(500).json({ message: '提交失败' });
//     }
//
//     console.log('插入成功，返回的结果:', result); // 记录成功插入后的结果
//     res.status(200).json({ message: '提交成功' });
//   });
// });

module.exports = router;
