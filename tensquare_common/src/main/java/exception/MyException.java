package exception;

/**
 * 自定义异常：终止已知不符合业务逻辑的操作的继续执行
 *
 * 编码规范：
 * 1. 注释 类，方法，属性，代码. 问为什么？回答的内容就是注释
 * 2. 开发中使用debug启动，下断点调试
 * 3. 驼峰命名规范，不要写中文，可以写拼音,不要超过30个字符
 * 4. sql: 不要select *
 * 5. 尽量使用常量且注释来代替硬编码（写死的值) 魔鬼数字/字符串  if(1==type){}
 * 6. 类中的方法的代码行，不要超过80行
 * 7. 首行缩进 空格/制表符 4个空格
 * 8. 代码要格式化
 * 9. 先处理错误判断，再进行正确代码的执行。让代码执行效率更高
 */
public class MyException extends RuntimeException {

    public MyException(String message){
        super(message);
    }

}
