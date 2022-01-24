package com.laowang.containers;

/*
* 各种容器，以及演变过程，示例代码，详见mashibing/JUC
*
* Vector                  ：读写都枷锁
* HashTable               ：很老的容器，都是synchronized修饰的方法
* HashMap                 ：又为了提高效率取消了synchronized，遍历效率比较低
* LinkedHashMap           ：叶子节点加了个链表，提高遍历效率
* TreeMap                 :内部红黑树，可以“排序”，自平衡，但是不适用高并发场景
* -----> Collections.synchronizedMap(new HashMap()).var = SynchronizedHashMap  ：HashMap的synchronized版本
* ConcurrentHashMap       ：并发场景下的Hashmap，CAS操作，读很快，写可能比HashTable还慢，但是并发业务大多是读，具体还是看需求
* 为啥没有ConcurrentTreeMap ？？？ 因为CAS在Tree上的操作太复杂，所以没有 并发版本的TreeMap
* ConcurrentSkipListMap   ：为了弥补ConcurrentHashMap 无法排序的问题，这个数据结构称为【跳表】
* CopyOnWriteArrayList    ：【写时复制】写时候复制一份新数组长度加1，写加synchronized锁，读不加锁，比Vector读的时候效率高
*                           所以读特别多，写特别少的时候用CopyOnWriteArrayList
* Queue                   ：根Queue接口，包含add(), offer(),peak(),poll(),add方法，满了会报异常
* ConcurrentLinkedQueue   ：offer()添加（不报错）, poll()队列头并从中删除,peek()取到队列头但是不删除
* LinkedBlockingQueue     ：无界队列，可以一直装，内存满了都不管,put()满了阻塞，take()空了阻塞，真正实现了blocking阻塞，
*                          底层使用了LockSupport.part()实现阻塞，因为时无界队列，不存在put阻塞
* ArrayBlockingQueue      ：有界队列，指定容量，满了或者空了都会阻塞，
* 以下均是BlockingQueue
* DelayQueue              ：按时间实现优先级的排列，Task类必须实现Delay接口，并重写compareTo()，DelayQueue.put(task对象)
* PriorityQueue           ：元素可以默认自然排序或者通过提供的Comparator（比较器）在队列实例化的时排序,
*                          PriorityQueue是非线程安全的，所以Java提供了PriorityBlockingQueue,默认升序,可以自己实现DIY排序
* SynchronousQueue        ：自己想实现Thread Pool 线程池时候，经常会用到，这个线程最终容量，稳定在0，往里put()
*                          一定要take()取走，size()恢复到0才能结束阻塞的状态，如果用add()方法往里添加东西，
*                          只加不取，就会报错！看似没用，实则JUC线程池里调度啊取任务啊交换啊用的都是它，
*                          等待一个线程取走。就是【手递手】
* TransferQueue           ：SychronousQueue的升级版，也用来传递内容，可指定长度，transfer(数据)阻塞，等有人取走
*                          等结果才继续走，所以消费者线程要在transfer()方法之前就启动才不会阻塞住。场景1：我付了帐交了钱，
*                          有人处理了订单了，才会给客户一个反馈,有人处理了。。【注意】现在一般不自己写底层的消息队列，直接用
*                          各种【MQ】Message Queue框架！如果自己写底层消息队列的话，推荐用TransferQueue
*                          等待多个线程取走。升级版的【多人对多人的手递手】
*   【作业】 用TransferQueue实现2个线程的交替打印：1,A,2,B,3,C.....
* 。。。。。。
* 直到为了多线程高并发设计的Queue。。
*
* 什么时候用啥容器，得写好几种方案，
* 当代码执行时间长，并发量不大的情况下，用synchronized; 当代码量少执行超快，并发却超高时，就用ConcurrentHashMap或者Queue等等
* 挨个压测，还有其它各项测试才行，
* 比如一块功能方法，先写好业务代码，里面容器部分就像填空选择题一样，多写几种，根据场景选择合适且安全的一个，或者
* 动态选择容器【这个很高大上】：面向接口的设计，等运行时候才会确定那个合适的容器，达到灵活切换实现类的目的
*
* */