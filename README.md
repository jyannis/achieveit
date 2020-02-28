# commit规范
 type: description

type：

        feat:  新功能
        update:  更新
        refactor :  已有功能重构
        fix:  修复bug
        perf :  性能优化

        style :  代码格式改变
        test:  UT
        build:  构建工具或构建过程等的变动
        revert:  撤销上一次的commit
        docs :  文档

        merge :  纯粹分支合并

如: [feat]增加成本中心授权人联动。
&nbsp;
&nbsp;
# 开发注意事项
### 通用
1. 在每个方法上加doc注明@author和修改时间。如果整个类都是自己写的，可以不在方法上注明，转为在类上注明。
2. 遇到运行时异常直接抛出RRException，在ExceptionTypeEnum中自行添加。
&nbsp;
### Controller
1. Controller只做鉴权、验参、调用service，即使透传也不直接调用mapper。
2. Controller方法返回值一致设为Object，便于切面后续调用封装为统一返回体。
3. 每一个需要鉴权且不是要求为项目经理（PROJECT_MANAGER）的控制层方法，都应该显式传projectId参数。
projectId应该显式放在query中，不能在body里。projectId可以使用@RequestParam修饰，也可以不用任何注解修饰。
4. 控制器上加上@Auth注解即默认需要登录才能访问。注解里添加相关属性即细粒度要求权限。
5. 控制方法除projectId必须固定外，其他参数都可以自由发挥、自由修改，记得同步修改swagger2信息即可。
&nbsp;
### Service
1. Service全部要做接口，接口上方法打注释写方法含义。
3. 每个Service完成单元测试。覆盖度要求90%方法覆盖和代码覆盖，需要有mock。
&nbsp;
&nbsp;
# 参考文档
### 通用Mapper
#### API
https://mapperhelper.github.io/all/
#### 检查通用Mapper的拼接是否符合预期
从MySQL查阅执行的SQL语句。
参考https://zhuanlan.zhihu.com/p/98641041
&nbsp;
&nbsp;
# 版本号
| 依赖 | 版本 |
| ------ | ------ |
| Java | 8 |
| SpringBoot | 2.x |
| MySQL | 5.7.* |

