!script {{  
  --/|Script Name : Dawnbringer
  --/|Version     : 1.1
  --/|Requires SC : 1.3.7+, TokenMod, NoteLog
  --/|Author      : Will M.

  --/|Description : Implements the effects of Dawnbringer sword
  --/|Updates     : 1.1 - Improved look of Light Aura by using combination of Aura1 and bright/low light radius

  --#title|Dawnbringer
  --#reentrant|Dawnbringer
  --#leftsub|
  --#rightsub|
  --#titlecardbackground|LightYellow
  --#titlefontsize|1.5em
  --#titlefontlineheight|2.0em
  --#titlefontface|Contrail One
  --#bodyfontsize|11px
  --#bodyfontface|Tahoma
  --#oddrowbackground|#00000000
  --#oddrowfontcolor|#000000
  --#evenrowbackground|#00000000
  --#evenrowfontcolor|#000000
  --#buttonbackground|DarkRed
  --#buttontextcolor|White
  --#buttonbordercolor|#999999
  --#buttonfontsize|10px
  --#buttonfontface|Tahoma
  --#dicefontcolor|#1C6EA4
  --#dicefontsize|3.0em
  --#usehollowdice|0
  --#emotebackground|#f5f5ba
  --#emotestate|visible
  --#tableborderradius| 6px;
  --#tableshadow| 5px 3px 3px 0px #aaa;
  --#bodybackgroundimage| linear-gradient( to bottom, LightYellow, Yellow, Orange, DarkOrange)
  --#timezone|America/Chicago  

  --#sourcetoken|@{selected|token_id}
  --#debug|1
  --:TOP|
  --#whisper|self
  --#emotetext|
  --#hidecard|0

  --&TokenId|@{selected|token_id}

  --+|[c][rbutton]Activate (Bonus Action)::ACTIVATE[/rbutton][/c]
  --+|[c][rbutton]Deactivate (Bonus Action)::DEACTIVATE[/rbutton][/c]
  --+|[br]
  --+|[c][rbutton]More Light (Action)::INCREASE;[&TokenId][/rbutton][/c]
  --+|[c][rbutton]Less Light (Action)::DECREASE;[&TokenId][/rbutton][/c]
  --+|[br]

  --/|+|[c][rbutton]Hide Aura::HIDE_FOR_ALL[/rbutton]&nbsp;&nbsp;[rbutton]Hide Aura for Others::HIDE_FOR_OTHERS[/rbutton][/c]
  --/|+|[c][rbutton]Show Aura::SHOW[/rbutton][/c]
  --/|+|[br]

  --+|[c][rbutton]Cast Lesser Restoration (Action)::LESSER_RESTORATION[/rbutton][/c]
  --+|[c][i]You touch a creature and can end either [b]one disease[/b] or [b]one condition[/b] afflicting it. The condition can be [b]blinded[/b], [b]deafened[/b], [b]paralyzed[/b], or [b]poisoned[/b].[/i][/c]
  --+|[BR]

  --+|[c][rbutton]Description::DESCRIPTION[/rbutton][/c]

  --x|

  --/| Drawing does these things: 
  --/| 1) Creates an aura 15 Light and 15 dim light
  --/| 2) Need to adjust token light settings
  --:ACTIVATE|Bonus Action
    --#hidecard|1
    --@token-mod|_on showplayers_aura1 _set aura1_radius|15 aura1_color|#f1c232 
    --@token-mod|_on emits_bright_light emits_low_light _set bright_light_distance#15 low_light_distance#15
    --&LogMsg|[*S:t-name] activates Dawnbringer
    -->LOG|[&LogMsg]
  --X|

  --/| Donning does these things: 
  --/| 1) Removes Aura
  --/| 2) Removes Token project light
  --:DEACTIVATE|Bonus Action
    --#hidecard|1
    --/|@token-mod|_off showplayers_aura1 showplayers_aura2 emits_bright_light emits_low_light _set aura1_radius|0 aura2_radius|0 bright_light_distance#0 low_light_distance#0
    --@token-mod|_off showplayers_aura1 showplayers_aura2 emits_bright_light emits_low_light _set aura1_radius|0 aura2_radius|0 bright_light_distance#0 low_light_distance#0
    --&LogMsg|[*S:t-name] dons Dawnbringer
    -->LOG|[&LogMsg]

  --X|

  --:INCREASE|Action
    --#hidecard|1
    --&TokenId|[&reentryval]
    --&Radius|[*[&TokenId]:t-bright_light_distance]
    --?[&Radius] -ge 30|INC_DONE
      --@token-mod|_set aura1_radius|+5 
      --@token-mod|_set bright_light_distance|+5 low_light_distance|+5
    --:INC_DONE|
  --X|

  --:DECREASE|Action
    --#hidecard|1
    --&TokenId|[&reentryval]
    --&Radius|[*[&TokenId]:t-bright_light_distance]
    --?[&Radius] -le 10|DEC_DONE
        --@token-mod|_set aura1_radius|-5 
        --@token-mod|_set bright_light_distance|-5 low_light_distance|-5
    --:DEC_DONE|
  --X|

  --:HIDE_FOR_OTHERS|Action
    --#hidecard|1
    --@token-mod|_off showplayers_aura1 showplayers_aura2 
  --X|

  --:HIDE_FOR_ALL|Action
    --#hidecard|1
    --@token-mod|_off showplayers_aura1 showplayers_aura2 _set aura1_color|transparent aura2_color|transparent
  --X|

  --:SHOW|Action
    --#hidecard|1
    --@token-mod|_on showplayers_aura1 showplayers_aura2 _set aura1_color|#FAFCDC aura2_color|#FBFCEF
  --X|

  --:LESSER_RESTORATION|Action
    --#Title|Cast Lesser Restoration
    --#hidecard|0
    --I;Let's do some magic healing|t;Target;Target of Lesser Restoraton?
    --/|*Target|[&Target]

    --&tStyle|style="width:100%;padding:1px;border-spacing:0px;border-collapse:collapse;text-shadow: 0px 0px 0px black;border:0px dashed black;"
    --&trStyle1|style="border:0px dashed black;"
    --&tdStyle1|style="width:50%;text-align:left;background-color:#FFFFFF;font-size:100%"
    --&tdStyle2|style="width:50%;text-align:left;background-color:#FFFFFF;font-size:100%"

    --&HasCond|FALSE
    --~|array;statusmarkers;aryToken_SM;[&Target]
    --/|Loop through all of the status markers on the targetted token 
    --~SMItem|array;getfirst;aryToken_SM

      --?[&SMItem] -eq ArrayError|ENDLOOP
      --:LOOPCHECK|

        --&SMItem_Name|[&SMItem]
        --/| The rbutton syntax doesn't like '::' in the name- messes with the parser
        --~SMItem|string;replace;::;^^;[&SMItem]
        --~SMItem_Name|string;before;::;[&SMItem_Name]

        --?Diseased -eq [&SMItem_Name]|SHOW_CONDITION
        --?Blinded -eq [&SMItem_Name]|SHOW_CONDITION
        --?Deafened -eq [&SMItem_Name]|SHOW_CONDITION        
        --?Paralyzed -eq [&SMItem_Name]|SHOW_CONDITION
        --?Poisoned -eq [&SMItem_Name]|SHOW_CONDITION

        --^NEXT_CONDITION|

        --:SHOW_CONDITION|
          --+|[c][rbutton][sm width=25px][&SMItem_Name][/sm]&nbsp;[&SMItem_Name]::UNMARK;[&Target]\[&SMItem]\[&SMItem_Name][/rbutton][/c]
          --&HasCond|TRUE

        --:NEXT_CONDITION|
        --~SMItem|array;getnext;aryToken_SM
      --?[&SMItem] -ne ArrayError|LOOPCHECK
      --:ENDLOOP|

      --?[&HasCond] -eq TRUE|LR_DONE
    
        --#Title|Unable to Heal
        --+|[b][*[&Target]:t-name][/b] does not appear to have a condition that can be healed by Dawnbringer.

        --+|[b]Dawnbringer's Lesser Restoration[/b] ability can remove one of the following: 
        --+|&nbsp;&nbsp;&nbsp;[b]- A disease[/b] 
        --+|&nbsp;&nbsp;&nbsp;[b]- Blinded[/b]
        --+|&nbsp;&nbsp;&nbsp;[b]- Deafened[/b]
        --+|&nbsp;&nbsp;&nbsp;[b]- Paralyzed[/b]
        --+|&nbsp;&nbsp;&nbsp;[b]- Poisoned[/b]

    --:LR_DONE|

    --+|[BR]
    --+|[c][rbutton]Return::TOP[/rbutton][/c]

  --X|

  --:UNMARK|
    --#hidecard|0
    --#Title|You've been Healed
    --#whisper|

    --/+Debug-RV|[&reentryval]
    --~Arg|string;split;\;[&reentryval]
    --&TokenId|[&Arg1]
    --&SMItem|[&Arg2]
    --&SMItemName|[&Arg3]    
    --~SMItem|string;replace;^^;::;[&SMItem]
    --&SMItem|-[&SMItem]

    --#emotetext|The power of Dawnbringer casts Lesser Restoration on [*[&TokenId]:t-name]
    --#targettoken|[&TokenId]

    --@token-mod|_set statusmarkers|[&SMItem] _ids [&TokenId] _ignore-selected

    --+|[j]The Sword known as Dawnbringer touches you, and a great wave of healing energy corses through your body.  A kind, feminine voice speaks to you: [/j] [br]

    --+|[j]"[b][*[&TokenId]:t-name][/b], you have been healed and are no longer [b][&SMItemName][/b].  Now go forward and fight for good and the light against creatures of darkness!"[/j]

    --&LogMsg|[*S:t-name] heals [*[&TokenId]:t-name] from being **[&SMItemName]**
    -->LOG|[&LogMsg]

  --X|

  --:DESCRIPTION|
  --#hidecard|0
  --+|Lost for ages in the Underdark, Dawnbringer appears to be a gilded longsword hilt. While grasping the hilt, you can use a bonus action to cause a blade of pure radiance to spring from the hilt, or cause the blade to disappear. While the blade exists, this magic longsword has the finesse property. If you are proficient with shortswords or longswords, you are proficient with Dawnbringer.
  --+|You gain a +2 bonus to attack and damage rolls made with this weapon, which deals radiant damage instead of slashing damage. When you hit an undead with it, that target takes an extra 1d8 radiant damage.
  --+|The sword's luminous blade emits bright light in a 15-foot radius and dim light for an additional 15 feet. The light is sunlight. While the blade persists, you can use an action to expand or reduce its radius of bright and dim light by 5 feet each, to a maximum of 30 feet each or a minimum of 10 feet each.
  --+|While holding the weapon, you can use an action to touch a creature with the blade and cast lesser restoration on that creature. Once used, this ability can't be used again until the next dawn.
  --+Sentience.|Dawnbringer is a sentient neutral good weapon with an Intelligence of 12, a Wisdom of 15, and a Charisma of 14. It has hearing and darkvision out to a range of 120 feet. The sword can speak, read, and understand Common, and it can communicate with its wielder telepathically. Its voice is kind and feminine. It knows every language you know while you’re attuned to it.
  --+Personality.|Forged by ancient sun worshipers, Dawnbringer is meant to bring light into darkness and to fight creatures of darkness. It is kind and compassionate to those in need, but fierce and destructive to its enemies.
  --+|Long years lost in darkness have made Dawnbringer frightened of both the dark and abandonment. It prefers that its blade always be present and shedding light in areas of darkness, and it strongly resists being parted from its wielder for any length of time.
  --+|[c][rbutton]Return::TOP[/rbutton][/c]
  --x|

  --:LOG|Text to log
    --~DT|system;date;getdatetime
    --@note-log|[&DT]: [%1%]
  --<|
}}

 