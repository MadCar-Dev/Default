!script {{  
  --/|Script Name : Combat Macros
  --/|Version     : 4.0
  --/|Requires SC : 1.3.7+, Mule character sheet with referenced macros installed
  --/|Author      : Will M.

  --/|Description : A quick menu system agregating a number of useful combat macros
  --/|              Groupinit, Kill token, turnorder, checks, apply/clear markers, 
  --/|              enlarge/reduce, spell templates, wild magic, party health

  --#title|Combat Macros
  --#titleCardBackground|#932729
  --#titlefontface|Patrick Hand
  --#reentrant|CombatMacros
  --#whisper|gm
  --#emoteState|1
  --#oddRowBackground|#FFFFFF
  --#evenRowBackground|#FFFFFF
  --#buttonbackground|#FFFFFF
  --#buttontextcolor|#0905f2
  --#buttonbordercolor|#FFFFFF
  --#bodyFontSize|11px
  --#debug|0

  --Ssettings|General Macro Tools

  --+[c][#C43112]🎬 Turn Order 🎬[/#][/c]|
  --+|[button]➕::~Mule|GroupInit[/button] Add selected tokens(multi)
  --+|[button]❌::~Mule|Kill[/button]/[button]➖::~Mule|TurnOrder-Remove[/button] Kill/Remove Token

  --+|[button]🔂::~Mule|TurnOrder-RoundCounter[/button] Add round tracker 
  --+|[button]⏳::~Mule|TurnOrder-AddCountdownItem[/button] / [button]🔠::~Mule|TurnOrder-AddCustItem[/button] Add Countdown/Custom

  --+|[button]⏬::~Mule|GrpInit-Sort[/button]/[button]🗑️::~Mule|GrpInit-Clear[/button] Sort / Clear Turnorder
  --+|[button]❓::~Mule|GrpInit-Help[/button] Show the help page

--+[c][#C43112]🏹 During Battle 🏹[/#][/c]|
  --+|[button]✔️::~Mule|GroupCheck[/button][button]🛡️::~Mule|GroupSave[/button] Group [b]Check[/b]/[b]Save[/b]
  --+|[button]😇::~Mule|Markers-Set-Status[/button] Set Status/Condition
  --+|[button]➖::~Mule|Markers-Unmark[/button]Unmark Status/Condition
  --+|[button]⏫::~Mule|Size-Enlarge[/button]/[button]⏬::~Mule|Size-Reduce[/button] Grow/Shrink

  --+|[button]💥::~Mule|1-Spell-Asset-and-AoE-5e[/button] 5e Spell Asset or Template
  --+|[button]🔵::~Mule|1-Spell-AoE-Generic[/button] Generic Spell AoE Template
  --+|[button]✨::~Mule|Wild-Magic-Surge[/button] Wild magic surge
  --+|[rbutton]&#x1F497;::PARTY_HEALTH_REPORT[/rbutton] Party Health Report

  -->FOOTER_BUTTONS_MAIN|

--X|

--/|==================  Utility Functions =======================
--:FOOTER_BUTTONS_MAIN|
    --+|[r][button]NPC::~Mule|NPC-Tools[/button]
            [button]DM::~Mule|DM-Tools[/button]
            [button]&#x1F504;::~Mule|Menu-TurnOrder-Macros[/button][/r]  
--<|

--:SHORT_NAME|Shortens player name for reporting purposes (6 characters); Parameter:Character_Name
  --~gSN|string;left;8;[%1%]
  --~gSN|string;before; ;[&gSN]
--<| Return

--:FIND_TOKEN|TokenId
  --#hidecard|1  
  --@ping-token|_[&reentryval] _[&SendingPlayerID]
  --X|
--<|

--/|==================  Party Health Report =======================
--:PARTY_HEALTH_REPORT| (PH_)
  --#hidecard|0
    
  --#title|Party Health
  --#leftsub|
  --#rightsub|
  --#oddRowBackground|#d8d8e6
  --#evenRowBackground|#ffffff

  --~tokencnt|array;pagetokens;alltokens;@{selected|token_id}

  --/|Loop through all of the tokens in "alltokens" 
  --~TokenId|array;getfirst;alltokens

  --&CID|[*[&TokenId]:t-represents]
  --?[&TokenId] -eq ArrayError|PH_ENDLOOP

  --&tbl|[t cellpadding="15" border="1" style="width:100%"][tr][td style="width:20%;text-align:left"][b]Char[/b][/td][td style="width:15%;text-align:center"][b]AC[/b][/td][td style="width:15%;text-align:center"][b]PP[/b][/td][td style="width:30%;text-align:center"][b]HP/Max[/b][/td][td style="width:20%;text-align:center"][b]Mad Lvl[/b][/td] [/tr]

  --:PH_LOOPCHECK|
    --&CID|[*[&TokenId]:t-represents]

    --/|Skip targets that are not on the token layer or that don't represent creatures
    --?[*[&TokenId]:t-layer] -ne objects|PH_CONTINUE
    --?"[*[&TokenId]:t-represents]" -ninc "-"|PH_CONTINUE
    --?"[*[&TokenId]:npc]" -eq 1|PH_CONTINUE

    --&CID|[*[&TokenId]:t-represents]
    -->SHORT_NAME|[*[&CID]:character_name]

    --&tbl|+ [tr][td style="width:20%;text-align:left"][b][&gSN][/b][/td][td style="width:15%;text-align:center"][*[&CID]:ac][/td][td style="width:15%;text-align:center"][*[&CID]:passive_wisdom][/td][td style="width:30%;text-align:center"][*[&CID]:hp] / [*[&CID]:hp^][/td][td style="width:20%;text-align:center"][*[&CID]:madness_level][/td] [/tr]

    --:PH_CONTINUE|
    --~TokenId|array;getnext;alltokens
    --?[&TokenId] -ne ArrayError|PH_LOOPCHECK
  --:PH_ENDLOOP|

  --&tbl|[&tbl] [/t]
  --+|[&tbl]
-->FOOTER_BUTTONS_MAIN|

--X|

}}