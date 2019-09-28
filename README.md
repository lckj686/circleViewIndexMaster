### 一、概述
项目中UI 给了个图是两个圆圈的上下边沿指示器：效果如下
![在这里插入图片描述](https://github.com/lckj686/circleViewIndexMaster/blob/master/3.gif)
实际在项目中废弃了，并没有使用到，有点可惜，记录下。

### 二、原理
原理：onDraw + 画曲线 + onTouch 手势
曲线的画法：
- 一开始想用赛贝尔曲线画，但是效果不理想，主要是自己也不大能驾驭
- 后面改用画半圆，直线相切的方式画弧线（但是ui 觉得太圆了）
- 最后使用比较简单的方法， 在目标图上标出了有限的一些点用平滑的曲线进行连接

交叉区域画法：
- 先找出交叉点
- 让交叉点和其余点用平滑的线进行连接

### 三、标注
![在这里插入图片描述](https://github.com/lckj686/circleViewIndexMaster/blob/master/1.jpg)  
  
  
![在这里插入图片描述](https://github.com/lckj686/circleViewIndexMaster/blob/master/2.jpg)
### 四、付
[github 地址]：[https://github.com/lckj686/circleViewIndexMaster](https://github.com/lckj686/circleViewIndexMaster)
