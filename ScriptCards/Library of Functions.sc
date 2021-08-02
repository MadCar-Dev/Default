
--/| Collection of common functions I might use in my various scripts
--/| need to better organize and document these at some point. 


--:FOOTER_BUTTONS|
    --+|[r][button]NPC::~Mule|NPC-Tools[/button][button]DM::~Mule|DM-Tools[/button][button]&#x1F504;::~Mule|NPC-Layers[/button][/r]  
--<|

--:FIND_TOKEN|TokenName
  --#hidecard|1  
  --@ping-token|_[&reentryval]
  --X|
--<|

--:SET_LAYER|TokenName, layer
  --#hidecard|1
  --~Arg|string;split;\;[&reentryval]
  --LOG|2;Set_Layer; RentryVal: [&reentryval]
  --LOG|2;Set_Layer; A1:[&Arg1] A2:[&Arg2]
  --@token-mod|_set layer|[&Arg2] _ids [&Arg1] _ignore-selected
  --X|
--<|

--:DEBUG_LOG|debug level;hdr;msg
  --\|debug level is used to show more or less debug messages.  1 is high level, 2 is detailed ...
  --*[%1%]|[%2%]; [%3%]
  
  --?[%1%] -gt [&DBUG]|LOG_EXIT
    --*[%2%]|[%3%]
  --:LOG_EXIT|
--<|

--:DNDBEYOND_MONSTER_LINK|MonsterName, button caption 
  --/|Format is https://www.dndbeyond.com/monsters/vampire-spellcaster
  --~MN|string;replace; ;-;[%1%]
  --~MN|string;replace; ;-;[&MN]
  --~MN|string;replace; ;-;[&MN]  
  --&zDnDBeyondMonsterLink|https://www.dndbeyond.com/monsters/[&MN]
  --&DNDBEYOND_MONSTER_BTN|[button][%2%]::[&zDnDBeyondMonsterLink][/button]
--<|

--:DNDBEYOND_SPELL_LINK|SpellName, button caption 
  --/|Format is https://www.dndbeyond.com/spells/dominate-person
  --~SN|string;replace; ;-;[%1%]
  --~SN|string;replace; ;-;[&SN]
  --~SN|string;replace; ;-;[&SN]
  --&zDnDBeyondSpellLink|https://www.dndbeyond.com/spells/[&SN]
  --&DNDBEYOND_SPELL_BTN|[button][%2%]::[&zDnDBeyondSpellLink][/button]  
--<|

--:SECTION_HEADER|Title
  --&hdrstyle_T|style="width:100%;padding:1px;border-spacing:0px;border-collapse:collapse;text-shadow: 0px 0px 0px black;border:1px solid black;"
  --&hdrstyle_TR|style="border:0px solid black;"
  --&hdrstyle_TD|style="width:100%;background-color:#edf7f0;font-size:110%;font-weight:bold;text-align:center"
  --+|[t [&hdrstyle_T]][tr [&hdrstyle_TR]][td [&hdrstyle_TD]][c][%1%][/c][/td][/tr][/t]
--<|

--:IS_TOKEN_IN_RANGE|Anchor Token, Checked Token, Range (in feet)
  --&AnchorToken|[%1%]
  --&TestToken|[%2%]
  --=Range|[%3%]
  --&IN_RANGE|0
  --/:CHECK DISTANCE IN ft.|

  --~d|euclideanlong;[&AnchorToken];[&TestToken]
  --=SnapInc|[*P:snapping_increment] --~SnapInc|math;max;.1;[$SnapInc]
  --=Scale|[*P:scale_number] --~Scale|math;max;1;[$Scale]
  --=DIST|[$d] / [$SnapInc] * [$Scale] --~DIST|math;round;[$DIST]

  --?[$DIST] -gt [$Range]|&IN_RANGE;0|&IN_RANGE;1
  --<|Return 

--:ADD_POS_SIGN|Parameter1: Number / Can be a roll or string variable, but should be a number. 
 --/| Populates zRET(String) and rRET(Roll Variable) (Global vars effectively)
  --&zRET|[%1%]
  --=rRET|[%1%]
  --?[%1%] -le 0|_APS_SKIP  --=rRET|&#43;[%1%] --&zRET|[$rRET.Text] --:_APS_SKIP|
--<|

--:CHECK_DAMAGE_MODS|damageVariableName;damageType
--&[%1%]|

  --/|*|[*T:t-name] immune to: [*T:npc_immunities] vs. [%2%]
--?"[*T:npc_vulnerabilities]" -inc "[%2%]"|>CDM_ISVULNERABLE;[%1%]
--?"[*T:npc_resistances]" -inc "[%2%]"|>CDM_ISRESISTANT;[%1%]
--?"[*T:npc_immunities]" -inc "[%2%]"|>CDM_ISIMMUNE;[%1%]
--<|
--:CDM_ISVULNERABLE| --&[%1%]| * 2 [Vulnerable] --/|*[Vulnerable] to [%2%] --<|
--:CDM_ISRESISTANT| --&[%1%]| \ 2 [Resistant] --/|*[Resistant] to [%2%] --<|
--:CDM_ISIMMUNE| --&[%1%]| * 0 [Immune] --/|*[Immune] to [%2%] --<|

--:LOG_NOTE|Text to log
  --~DT|system;date;getdatetime
  --@note-log|[&DT]: [%1%]
--<|