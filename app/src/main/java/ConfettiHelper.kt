//package com.example.employee_gamification
//
//import nl.dionsegijn.konfetti.core.*
//import nl.dionsegijn.konfetti.core.emitter.Emitter
//import java.util.concurrent.TimeUnit
//
//fun createLeftBlast(): Party {
//    return Party(
//        speed = 0f,
//        maxSpeed = 30f,
//        damping = 0.9f,
//        spread = 180,
//        colors = listOf(0xfce18a, 0xff726d),
//        position = Position.Relative(0.0, 0.5),
//        emitter = Emitter(duration = 2, TimeUnit.SECONDS).perSecond(100),
//        timeToLive = 2000L
//    )
//}
//
//fun createRightBlast(): Party {
//    return Party(
//        speed = 0f,
//        maxSpeed = 30f,
//        damping = 0.9f,
//        spread = 180,
//        colors = listOf(0xf4306d, 0xb48def),
//        position = Position.Relative(1.0, 0.5),
//        emitter = Emitter(duration = 2, TimeUnit.SECONDS).perSecond(100),
//        timeToLive = 2000L
//    )
//}
