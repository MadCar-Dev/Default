!script {{  
  
  --/|Script Name : Player Macros
  --/|Version     : 4.1
  --/|Requires SC : 1.3.7+, Mule character sheet with referenced macros installed
  --/|Author      : Will M.

  --/|Description : A quick menu system agregating a number of useful player facing macros
  --/|              gmsheet, set status/condition, Enlarge/Reduce, Wild magic, rest, healing, party funds, spell templates
  --/|Updates     : 4.1 - Added Potions/Scrolls report, AFK


  --#title|Player Macros
  --#reentrant|Player Macros
  --#titlefontface|Patrick Hand
  --#titleCardBackground|#932729
  --#whisper|self
  --#oddRowBackground|#FFFFFF
  --#evenRowBackground|#FFFFFF
  --#buttonbackground|#FFFFFF
  --#buttontextcolor|#0905f2
  --#buttonbordercolor|#FFFFFF
  --#bodyFontSize|11px
  --#debug|0

  --Ssettings|Player Macro Tools

  --+|[rbutton]🕵::LIST_PLAYERS[/Rbutton] Find My Friends
  --+|[b][button]AFK::~Mule|AFK[/button][/b] Away from Keyboard (Toggle)  

  --+|[button]📜::~Mule|GMSheet[/button] See Character [b]Cheat-Sheet[/b]
  --+|[button]😇::~Mule|Markers-Set-Status[/button] Set Status/Condition
  --+|[button]➖::~Mule|Markers-Unmark[/button]Unmark Status/Condition
  --+|[button]⏫::~Mule|Size-Enlarge[/button]/[button]⏬::~Mule|Size-Reduce[/button] Grow/Shrink

  --+|[button]✨::~Mule|Wild-Magic-Surge[/button] Wild magic surge
  --+|[button]🤪::~Mule|Madness[/button] Let's get crazy(Madness)  

  --+|[button]😴::~Mule|Rest-Short[/button] / [button]🏕️::~Mule|Rest-Long[/button] Rest Short/Long
  --+|[button]🧪::~Mule|Healing-Potion[/button] Healing potion
  --+|[button]🔥::~Mule|Set-Light[/button] / [button]🔦::~Mule|Lighting-Report[/button] Light Set/Report
  --+|[button]💰::~Mule|PartyFunds-Report[/button] Party Funds
  --+|[button]⚗️::~Mule|Potions[/button] Party Potions and Scrolls

  --+[c][#C43112]🎆 Magic Stuff 🎆[/#][/c]|
  --+|[button]📗::~Mule|SpellBook[/button] Spell Book
  --+|[button]💥::~Mule|1-Spell-Asset-and-AoE-5e[/button] 5e Spell Asset or Template
  --+|[button]🔵::~Mule|1-Spell-AoE-Generic[/button] Generic Spell AoE Template
  --/+|[button]🦊::~Mule|Spawn-Creature[/button] Spawn Magic Creature

--X|

--:LIST_PLAYERS|

  --#title|Find My Friends

  --~tokencnt|array;pagetokens;alltokens;@{selected|token_id};pc
  --/|Loop through all of the tokens in "alltokens" 
  --~TokenId|array;getfirst;alltokens
  --&CharId|[*[&TokenId]:t-represents]
  --?[&TokenId] -eq ArrayError|ENDLOOP

  --&TblStyle1|"width:100%;text-align:left;padding:1px;border-spacing:1px;border-collapse:collapse;text-shadow: 0px 0px 0px black;border: 0px dashed black;"
  --&tbl|[t style=[&TblStyle1]]

  --:LOOPCHECK|
    --&CharId|[*[&TokenId]:t-represents]
    --/+Debug|[*[&TokenId]:t-name]

    --/|Skip targets that are not on the token layer or that don't represent creatures
    --?[*[&TokenId]:t-layer] -ne objects|CONTINUE
    --/?"[*[&TokenId]:npc]" -eq 1|CONTINUE
    --/?"[*[&TokenId]:t-represents]" -inc "-"|START
    --?"[*[&CharId]:PlayerCharacter]" -eq "1"|START
    --^CONTINUE|
   --:START|

    --&tbl|+ 
        [tr][td style="width:100%;text-align:left;background-color:#FFFFFF"][b][rbutton][*[&CharId]:character_name]::FIND_TOKEN;[&TokenId][/rbutton][/b][/td][/tr]

    --:CONTINUE|
    --~TokenId|array;getnext;alltokens
    --?[&TokenId] -ne ArrayError|LOOPCHECK
  --:ENDLOOP|

  --&tbl|+ [/t] 
  --+|[&tbl]

  --X|
--<|

--:FIND_TOKEN|TokenId
  --#hidecard|1  
  --@ping-token|_[&reentryval] _[&SendingPlayerID]
  --X|
--<|

}}