package com.ubtrobot.smartprojector.ui.tuya

class TuyaDp {
    /**
     * mode : rw
     * code : work_mode
     * name : 模式
     * property : {"range":["white","colour","scene","scene_1","scene_2","scene_3","scene_4"],"type":"enum"}
     * iconname : icon-dp_mode
     * id : 2
     * type : obj
     */
    var mode: String? = null
    var code: String? = null
    var name: String? = null

    /**
     * range : ["white","colour","scene","scene_1","scene_2","scene_3","scene_4"]
     * type : enum
     */
    var property: Property? = null
    var iconname: String? = null
    var id = 0
    var type: String? = null

    class Property {
        /**
         * unit :
         * min : 25
         * max : 255
         * scale : 0
         * step : 1
         * type : value
         */
        var unit: String? = null
        var min = 0
        var max = 0
        var scale = 0
        var step = 0
        var type: String? = null
        var range: List<String>? = null
    }
}