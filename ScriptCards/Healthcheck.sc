!script {{  
  --/|Script Name : Health Check
  --/|Version     : 2.0
  --/|Requires SC : 1.3.7+, TokenMod, NoteLog, Chatsetattr, ping-token
  --/|Author      : Will M.

  --/|Description : Utility card to simulate a character attempting to understand the 
  --/|              health status of a friend or foe.

  --/| Purpose of this script is to allow a user to inspect a token's health status
  --/| based on the character's Perception and distance to target, they'll get different levels of ideas.  
  --/| It uses the character's Medicine and Perception abilities as an adder

  --#title|Health Check
  --#reentrant|HealthStatus @{selected|character_id}
  --#titleCardBackground|#932729
  --#whisper|self
  --#oddRowBackground|#FFFFFF
  --#evenRowBackground|#FFFFFF
  --#buttonbackground|#FFFFFF
  --#buttontextcolor|#0905f2
  --#buttonbordercolor|#FFFFFF
  --#bodyFontSize|11px
  --#sourceToken|@{selected|token_id}  
  --#targetToken|@{target|token_id}  
  --#activepage|[*S:t-_pageid]  
  --#debug|1
  --&DBUG|2
  --&DBUG_Msg|
  
  --/| First get the source and destination tokens

  --&STokenId|@{selected|token_id}
  --&TTokenId|@{target|token_id}
  -->LOG|2;;[c][b]Health Check[/b][/c]
  -->LOG|2;S=>T;[*[&STokenId]:t-name] => [*[&TTokenId]:t-name]

  --=TargetHP|[*[&TTokenId]:t-bar1_value]
  --=TargetMax|[*[&TTokenId]:t-bar1_max]
  --=TargetPct|[$TargetHP] / [$TargetMax] * 10000 \ 100

  --?[$TargetHP.Raw] -gt 0|ALIVE
    -->LOG|2;They are Dead; or at least appear to be dead
    --+|[c][img width=200][*T:avatar][/img][/c] 
    --+|[c][b]They are dead, or at least appear to be dead[/b][/c]
    -->LOG|2;Ping; '[rbutton]Target::FIND_TOKEN;[&TTokenId][/rbutton] [rbutton]Character::FIND_TOKEN;[&STokenId][/rbutton]'
    --X|


  --:ALIVE|

  --/| Compute the distance between the tokens to add a difficulty rating
  --/| 0-20        :Advantage
  --/| 20.1 - 40   :Normal
  --/| 40.1 - 100   :-1
  --/| 100.1 - 150  :-3
  --/| 150+        :Disadvantage

  --/:CHECK DISTANCE IN ft.|
  --~d|euclideanlong;[&STokenId];[&TTokenId]
  --=SnapInc|[*P:snapping_increment] --~SnapInc|math;max;.1;[$SnapInc]
  --=Scale|[*P:scale_number] --~Scale|math;max;1;[$Scale]
  --=DIST|[$d] / [$SnapInc] * [$Scale] --~DIST|math;round;[$DIST]

  --?[$DIST] -gt 150|&ChkDice;2d20kl1
  --?[$DIST] -le 150|&ChkDice;1d20 - 3
  --?[$DIST] -le 100|&ChkDice;1d20 - 1
  --?[$DIST] -le 40|&ChkDice;1d20 
  --?[$DIST] -le 20|&ChkDice;2d20kh1

  -->LOG|2;Dist;[$DIST]ft. Dice:[&ChkDice]


  --/| get medicine and perception skill bonuses
  --&PerceptionBonus|[*[&STokenId]:perception_bonus]
  --&MedicineBonus|[*[&STokenId]:medicine_bonus]

  --/| use 1/2 perception and 1/2 medicine skill as bonus to roll
  --=Bonus|[&PerceptionBonus] + [&MedicineBonus]
  --/|=Bonus|[$Bonus]/2
  -->LOG|2;Bonus;[$Bonus] or (Avg of Pcep:[&PerceptionBonus] and Med:[&MedicineBonus])

  --#rightsub|Bonus:[$Bonus]
  --#leftsub|Dist:[$DIST]ft. 

  --=CheckRoll|[&ChkDice] + [$Bonus]

  --/| Results
  --/| F 1-4: Total Failure (or natural 1)
  --/| E 4.1-9: Have no idea
  --/| D 9.1-14: a litle bit of a clue
  --/| C 14.1-18: Have a good idea
  --/| B 18.1-25: Pretty solid idea
  --/| A 25.1+:  Keenly aware (or natural 20)
  --?[$CheckRoll] -gt 25|&Grade;A
  --?[$CheckRoll] -le 25|&Grade;B
  --?[$CheckRoll] -le 18|&Grade;C
  --?[$CheckRoll] -le 14|&Grade;D
  --?[$CheckRoll] -le 9|&Grade;E
  --?[$CheckRoll] -le 4|&Grade;F

  --?[$CheckRoll.Base] -eq 1|&Grade;F
  --?[$CheckRoll.Base] -eq 20|&Grade;A

  --/|Grade   F                     E               D                 C                         B                         A         
  --/|A 100%  Death is close!       Looks bad       I have no idea    Looks Healthy             Looks Fit                 Perfect Health (100%)
  --/|B 75+%  They are going down!  Looks bad       I have no idea    Looks Healthy             Some scratches            Some scratches, but good (Less than 100%)
  --/|C 50+%  About to croak!       I have no idea  I have no idea    They're hurting           They're hurting           They've taken some damage (Less than 75%) 
  --/|D 25+%  About to croak!       I have no idea  I have no idea    They're hurting           They're hurting           Bloodied (Less than 50%)
  --/|E 10+%  About to croak!       Looks Ok        I have no idea    More than a flesh wound   Not long for this world   Loosing blood fast, one more solid hit should do them in (Less than 25%)
  --/|F 10+%  This one is strong!   Looks Ok        I have no idea    Close to falling          Death is close            On death's door (Less than 10%) 

  --?[$TargetPct] -ge 100|&Health;A
  --?[$TargetPct] -lt 100|&Health;B
  --?[$TargetPct] -lt 75|&Health;C
  --?[$TargetPct] -lt 50|&Health;D
  --?[$TargetPct] -lt 25|&Health;E
  --?[$TargetPct] -lt 10|&Health;F

  -->LOG|2;Roll:[$CheckRoll] ([$CheckRoll.Base]+[$Bonus]) -> Est:[&Grade] vs. Act:[&Health] 
  -->LOG|2;Actual;[$TargetHP]/[$TargetMax] ([$TargetPct]%)

  --&HText_AF|Death is close.                           --&HColor_AF|#cc0000 
  --&HText_AE|Hard to tell.                             --&HColor_AE|#ffffe6 
  --&HText_AD|I have no idea.                           --&HColor_AD|#ffffff
  --&HText_AC|Hard to tell.                             --&HColor_AC|#e6ffe6
  --&HText_AB|Looks fit to me.                          --&HColor_AB|#b3ffb3
  --&HText_AA|Perfect Health (100%)!                    --&HColor_AA|#006600

  --&HText_BF|They are going down soon!                 --&HColor_BF|#ff3333
  --&HText_BE|Looks bad, really bad!                    --&HColor_BE|#ff8080
  --&HText_BD|I have no idea.                           --&HColor_BD|#ffffff
  --&HText_BC|Looks Healthy.                            --&HColor_BC|#aaff80
  --&HText_BB|Some scratches.                           --&HColor_BB|#99ff66
  --&HText_BA|Some scratches, but good (Less than 100%) --&HColor_BA|#88ff4d

  --&HText_CF|About to croak!                           --&HColor_CF|#cc0000  
  --&HText_CE|I have no idea, maybe if he is hit again we can tell. --&HColor_CE|#ffffff
  --&HText_CD|I have no idea.                           --&HColor_CD|#ffffff
  --&HText_CC|They are hurting!                          --&HColor_CC|#ffffe6
  --&HText_CB|They are hurting!                          --&HColor_CB|#ffffb3
  --&HText_CA|They have taken some damage (Less than 75%). --&HColor_CA|#ffff99 

  --&HText_DF|Still standing for sure!                  --&HColor_DF|#ccffb3
  --&HText_DE|I have no idea.                           --&HColor_DE|#ffffff
  --&HText_DD|I have no idea.                           --&HColor_DD|#ffffff
  --&HText_DC|They are hurting.                          --&HColor_DC|#ffffb3
  --&HText_DB|It is just a flesh wound.                  --&HColor_DB|#ffd1b3
  --&HText_DA|Bloodied (Less than 50%).                 --&HColor_DA|#cc5200

  --&HText_EF|Doing OK I think!                         --&HColor_EF|#b3ffb3   
  --&HText_EE|Looks good to me.                         --&HColor_EE|#ccff99
  --&HText_ED|I have no idea.                           --&HColor_ED|#FFFFFF  
  --&HText_EC|A little more than a flesh wound.         --&HColor_EC|#ffe6e6
  --&HText_EB|Not long for this world.                  --&HColor_EB|#ff6666
  --&HText_EA|Loosing blood fast, one more solid hit should do them in (Less than 25%) --&HColor_EA|#ff1a1a

  --&HText_FF|This one is strong!                       --&HColor_FF|#009933
  --&HText_FE|Looks OK to me.                           --&HColor_FE|#ccffdd
  --&HText_FD|I have no idea.                           --&HColor_FD|#ffffff  
  --&HText_FC|They are going down.                      --&HColor_FC|#ffff1a
  --&HText_FB|Death is close, is that an arrow through their skull? --&HColor_FB|#660000
  --&HText_FA|On deaths door (Less than 10%)           --&HColor_FA|#cc0000

  --#oddRowBackground|[&HColor_[&Health][&Grade]]
  --#evenRowBackground|[&HColor_[&Health][&Grade]]


  -->LOG|2;Msg; HText_[&Health][&Grade] [&HText_[&Health][&Grade]]  

  -->LOG|2;Ping; '[rbutton]Target::FIND_TOKEN;[&TTokenId][/rbutton] [rbutton]Character::FIND_TOKEN;[&STokenId][/rbutton]'
  --+|[c][img width=200][*T:avatar][/img][/c] 
  --+|[c][b][&HText_[&Health][&Grade]][/b][/c]
  --X|

--:LOG|debug level;hdr;msg
  --\|debug level is used to show more or less debug messages.  1 is high level, 2 is detailed ...
  --\|*[%1%]|[%2%]; [%3%]
    --&Lvl|[%1%]
    --&Hdr|[%2%]
    --&Msg|[%3%]
    --?[&Lvl] -gt [&DBUG]|LOG_EXIT
      --*[&Hdr]|[&Msg]
    --:LOG_EXIT|
--<|

--:FIND_TOKEN|TokenId - Pings the token name
  --#hidecard|1  
  --/@find-token|_[&reentryval]
  --@ping-token|_[&reentryval]
  --X|
--<|

}}