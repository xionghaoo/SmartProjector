package com.ubtrobot.ifly

class WakeupResult {
    // {"sst":"wakeup", "id":0, "score":2276, "bos":920, "eos":1940 ,"keyword":"ke1-dou3-ke1-dou3"}
    var sst: String? = null
    var id: Int = 0
    var score: Int = 0
    var bos: Int = 0
    var eos: Int = 0
    var keyword: String? = null
}