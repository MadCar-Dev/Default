!scriptcard {{ 


  --/|Script Name : Decrease Madness Level
  --/|Version     : 2.0
  --/|Requires SC : 1.3.7+, ChatSetAttr
  --/|Author      : Will M.

  --/|Description : Based on Madness mechanic in OOtA - Decreases the madness level and tracks in a Madness_Level character attribute
  --/|               
  --/|     

  --#title|Decrease Madness 
  --#titleCardBackground|#932729
  --#oddRowBackground|#CEC7B6
  --#evenRowBackground|#B6AB91
  --#sourceToken|@{selected|token_id}
  --#hideCard|1
  --#whisper|gm

  --~|array;define;aMadLvl;0;1.1;1.2;1.3;2.1;2.2;2.3;3.1;3.2;3.3;4.1;4.2;4.3;5.1;5.2;5.3;6.1;6.2;6.3
  --~tmp|array;stringify;aMadLvl
  --+Debug|MadLvl Array: [&tmp]

  --/|Get current Madness_Level
  --&MadLvl|[*S:Madness_Level]
  --+Debug|Cur MadLvl: [&MadLvl]

  --/|Find position in ML array
  --~z|array;getfirst;aMadLvl

  --:LOOP|
  --+Debug|Looping: [&z] 
  --?[&z] -eq [&MadLvl]|FOUND
  --~z|array;getnext;aMadLvl
  --?[&z] -ne ArrayError|LOOP

  --:NOTFOUND|
  --@setattr| _charid [*S:character_id] _madness_level|0
  --X| Early Exit

  --:FOUND|
  --~z|array;getprevious;aMadLvl
  --?[&z] -eq ArrayError|NOTFOUND

  --/|Write Madness Level back
  --@setattr| _charid [*S:character_id] _madness_level|[&z]

  }}