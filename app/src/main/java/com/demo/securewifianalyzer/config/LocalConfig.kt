package com.demo.securewifianalyzer.config

object LocalConfig {
    const val email="whiteblack20210706@gmail.com"
    const val url="https://sites.google.com/view/securewifianalyzer/home"

    const val SWAN_START="swan_start"
    const val SWAN_HOME_NA="swan_home_na"
    const val SWAN_WIFI_NA="swan_wifi_na"
    const val SWAN_FUNC_NA="swan_func_na"
    const val SWAN_FUNCTION_IN="swan_function_in"
    const val SWAN_WIFICON_IN="swan_wificon_in"

    const val LOCAL_AD_STR="""
    {"swan_start":[
        {
            "swan_s":"admob",
            "swan_id":"ca-app-pub-3940256099942544/3419835294222",
            "swan_t":"open",
            "swan_l":2
        },
          {
            "swan_s":"admob",
            "swan_id":"ca-app-pub-3940256099942544/3419835294333",
            "swan_t":"open",
            "swan_l":3
        }
    ],
    "swan_home_na":[
        {
            "swan_s":"admob",
            "swan_id":"ca-app-pub-3940256099942544/2247696110",
            "swan_t":"native",
            "swan_l":2
        }
    ],
    "swan_wifi_na":[
        {
            "swan_s":"admob",
            "swan_id":"ca-app-pub-3940256099942544/2247696110",
            "swan_t":"native",
            "swan_l":2
        }
    ],
    "swan_func_na":[
        {
            "swan_s":"admob",
            "swan_id":"ca-app-pub-3940256099942544/2247696110",
            "swan_t":"native",
            "swan_l":2
        }
    ],
    "swan_function_in":[
        {
            "swan_s":"admob",
            "swan_id":"ca-app-pub-3940256099942544/8691691433222",
            "swan_t":"interstitial",
            "swan_l":2
        },
           {
            "swan_s":"admob",
            "swan_id":"ca-app-pub-3940256099942544/8691691433333",
            "swan_t":"interstitial",
            "swan_l":3
        }
    ],
    "swan_wificon_in":[
        {
            "swan_s":"admob",
            "swan_id":"ca-app-pub-3940256099942544/8691691433",
            "swan_t":"interstitial",
            "swan_l":2
        }
    ]
}"""
}