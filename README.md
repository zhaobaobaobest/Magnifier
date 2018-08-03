# Magnifier
Android放大镜效果
 
### 使用说明
    下载项目，引用ctmagnifier依赖包。放大当前显示页面用法：
    创建放大镜实例
    MagnifierView mv = new MagnifierView.Builder(MainActivity.this)
                .intiLT(100,200)
                .viewWH(320,320)
                .scale(2f)
                .alpha(16)
                .color("#ff00ff")
                .build();
    打开放大镜
    mv.startViewToRoot();
    关闭放大镜
    mv.closeViewToRoot();
   
    放大指定容器的用法：
    MagnifierView mv2 = new MagnifierView.Builder(MainActivity.this)
                .rootVg(rl)
                .viewWH(200,200)
                .build();
    rootVg指定需要显示放大镜的容器
   
    具体使用方法详见demo以及源码。
    效果图如下：
![效果图](https://github.com/zhaobaobaobest/Magnifier/blob/master/effect.gif)
