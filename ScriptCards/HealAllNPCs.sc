!script {{
  --/|Script  : Heal Everyone
  --/|Purpose : Asks for a target token and sets every token on that page to full HP.
  --/|Notes   : Requires a bar with a max value. Customize the "healthBar" var to the
  --/|          bar number that you use to represent health.
  --/|Requires: token-mod
  --&healthBar|1
  --#hidecard|1
  --~|array;pagetokens;alltokens;@{target|token_id};npc
  --~tokenid|array;getfirst;alltokens
  --?[&tokenid] -eq ArrayError|endOutput
  --:loopCheck|

  --/|Skip targets that are not on the token layer or that don't represent creatures
  --?[*[&tokenid]:t-layer] -ne objects|continue
  --?"[*[&tokenid]:t-represents]" -ninc "-"|continue

  -->SetHealthWithTokenMod|[&tokenid];[&healthBar];[*[&tokenid]:t-bar[&healthBar]_max]

  --:continue|
  --~tokenid|array;getnext;alltokens
  --?[&tokenid] -ne ArrayError|loopCheck

  --X|

  --:SetHealthWithTokenMod|Parameters are tokenid;bar#;amount
  --@token-mod|_ignore-selected _ids [%1%] _set bar[%2%]_value|[%3%] statusmarkers|-dead
  --<| 
}}