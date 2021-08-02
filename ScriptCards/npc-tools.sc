!scriptcard {{ 

  --/|Script Name : NPCTools
  --/|Version     : 4.1
  --/|Requires SC : 1.4.0+, TokenMod, NoteLog, Chatsetattr, ping-token, gmnote, tokenactions
  --/|Author      : Will M.

  --/|Description : A collection of utility functions against NPCs

  --/|Updates     : 4.1 - Added pagetokens prefilter (NPC) to speed up code; 
  --/|                    Cleaned up formatting

  --&reentryval|100
  --:TOP|
  --#reentrant|NPC Tools
  --#title|NPC Tools
  --#titleCardBackground|#095c1f
  --/|#titlecardbackgroundimage| linear-gradient( to bottom, #66FF66, #282828 )  

  --/#oddRowBackground|#9fbfa7  (Old background-color)

  --&FColor1|#0905f2
  --&FColor2|#000000
  --&BColor1|#BADFBB
  --&BColor2|#BADFBB

  --/|#titlecardbackgroundimage| linear-gradient(to bottom, #f0fff8, #282828 )   
  --#titlefontsize|1.0em
  --#titlefontlineheight|1.5em
  --/|#titlefontface|Courier
  --#norollhighlight| 0
  --#hideTitleCard| 0
  --#bodyfontsize| 12px
  --/|#bodyfontface|Courier
  --#oddrowbackground|[&BColor1]
  --#oddrowfontcolor|[&FColor2]
  --#evenrowbackground|[&BColor1]
  --#evenrowfontcolor|[&FColor1]
  --#buttonbackground|[&BColor1]
  --#buttontextcolor|[&FColor1]
  --#buttonbordercolor|[&BColor1]
  --#buttonfontsize|10px
  --/|#buttonfontface|Courier

  --#hidecard|0    
  --#whisper|gm
  --#debug|0
  --#timezone|America/Chicago
  --#sourceToken|@{selected|token_id}
  --#activepage|[*S:t-_pageid]

  --Ssettings|NPCTools

    --=Rng|[&reentryval]


    -->SECTION_HEADER|Range From Selected Token ([$Rng.Total] ft.) or Turnorder List
    --/| Ranges: 10, 20, 50, 100, 250, 500, All, Turnorder List
    --+|[c][rbutton]10::TOP;10[/rbutton]&nbsp;
           [rbutton]20::TOP;20[/rbutton]&nbsp;
           [rbutton]50::TOP;50[/rbutton]&nbsp;
           [rbutton]100::TOP;100[/rbutton]&nbsp;
           [rbutton]250::TOP;250[/rbutton]&nbsp;
           [rbutton]500::TOP;500[/rbutton]&nbsp; 
           [rbutton]All::TOP;1000[/rbutton]&nbsp;
           [rbutton]TO-List::TOP;TO[/rbutton][/c]

  -->SECTION_HEADER|Macros  
  --+|[c][button]&#x1F4CD;::~Mule|Page-Assets[/button]
         [button]&#x1F4CB;::~Mule|Menu-Char-Token-Info[/button]
         [button]&#x1F527;::~Mule|Menu-Utility-Macros[/button]
         [button]&#x1FE0F;::~Mule|Menu-Combat-Macros[/button]
         [button]&#x1F3AD;::~Mule|Player-Macros[/button]
         [rbutton]&#x2699;::GLOBAL_NPC_SETTINGS[/rbutton]
         [button]&#x1F310;::~Mule|DM-Links[/button]         
         [button]&#x1F39A;::~Mule|NPC-Layers[/button]
     [/c]

  --&tStyle|style="width:100%;text-align:left;padding:1px;border-spacing:0px;border-collapse:collapse;text-shadow: 0px 0px 0px black;border: 0px dashed black;"  
  --&trStyle1|style="border:1px dashed black;"
  --&trStyle2|style="border:0px dashed black;"
  --&tdStyle1|style="width:60%;text-align:left;background-color:[&BColor1];font-size:50%;"
  --&tdStyle2|style="width:40%;text-align:left;background-color:[&BColor1];font-size:50%;"
  --&tdStyle3|style="width:100%;background-color:edf7f0;font-size:100%;font-weight:bold;text-align:center;" colspan=2  
  
  --&ObjRows|F --&GMRows|F --&MapRows|F --&WallsRows|F --&OtherRows|F 
  --&OtherTbl| --&ObjTbl| --&GMTbl| --&MapTbl| --&WallsTbl|

  --/| *$Rng.Tex:|[$Rng.Text]
  --?[$Rng.Text] -eq TO|[
    --/|Loop through all of the tokens in the TurnOrder List
    --/|TO_GETFIRSTITEM populates the firest TokenID and sets gTO_ITEM_NDX to 1 if the list isnt empty
    -->TO_GETFIRSTITEM|
    --?[$gTO_ITEM_NDX] -eq 0|ENDLOOP
    --&TokenId|[&gTO_ITEM_TOKENID]
    --?[&gTO_ITEM_TYPE] -ne NPC|CONTINUE|START
  --]|[
    --/|Loop through all of the tokens in "AllTokens" 
    --~tokencnt|array;pagetokens;AllTokens;@{selected|token_id};npc
    --~TokenId|array;getfirst;AllTokens
    --?[&TokenId] -eq ArrayError|ENDLOOP
  --]|

  --?"[*[&TokenId]:playercharacter]" -eq 1|CONTINUE

  --:LOOPCHECK|
    --&CharId|[*[&TokenId]:t-represents]
    --/+Debug|[*[&TokenId]:t-name], [*[&TokenId]:t-layer], [*[&TokenId]:npc], [*[&TokenId]:t-represents]

    --/|Skip this token if it is out of the defined range 
    -->IS_TOKEN_IN_RANGE|@{selected|token_id};[&TokenId];[$Rng] --?[&IN_RANGE] -eq 0|CONTINUE


    --/|Skip targets that are not NPCs
    --/|?[*[&TokenId]:t-layer] -ne objects|CONTINUE
    --/|?"[*[&TokenId]:npc]" -ne 1|CONTINUE
    --/|?"[*[&TokenId]:t-represents]" -inc "-"|START
    --/|^CONTINUE|

   --:START|
    --&CharId|[*[&TokenId]:t-represents]

    --?"[*[&TokenId]:t-layer]" -eq "objects"|&visbtn;&#x1F648|&visbtn;&#x1F649

    -->DNDBEYOND_MONSTER_LINK|[*[&CharId]:npc_name];'&#x1F517;' 


    --/|&TNInfo|[i]ac [*[&TokenId]:npc_ac] hp [*[&TokenId]:t-bar1_value]/[*[&TokenId]:t-bar1_max][/i]
    --&TNInfo|[br][rbutton][+]::ADD_QUICK_NOTE;[&TokenId][/rbutton][*[&TokenId]:t-bar3_value]
    
    --/|?[$Rng.Text] -eq TO|&TNInfo;[&TNInfo][i] P-[&gTO_ITEM_POS][/i]

    --&t|[tr [&trStyle1]][td [&tdStyle1]] [img width=30 height=30][*[&TokenId]:t-imgsrc][/img]
          [b][rbutton][*[&TokenId]:t-name]::FIND_TOKEN;[&TokenId][/rbutton][/b][&TNInfo][/td]
          [td [&tdStyle2]]
            [rbutton][&visbtn];::TOGGLE_LAYER;[&TokenId][/rbutton]
            [rbutton]&#x1F4C3;::NPC_DETAILS_REPORT;[&CharId]\[&TokenId]\[/rbutton]
            [rbutton]&#x1F3F9;::NPC_FEATURES_REPORT;[&CharId]\[&TokenId]\[/rbutton]
            [rbutton]&#x1F4AB;::NPC_SPELLS_REPORT;[&CharId]\[&TokenId]\[/rbutton]
            [rbutton]&#x2623;::GMNOTE;[&TokenId]\Bio[/rbutton]
            [&DNDBEYOND_MONSTER_BTN]
            [rbutton]&#x1F4F7;::GMNOTE;[&TokenId]\Images[/rbutton]
            [rbutton]&#x1F469;&#x1F3FD;&#x200D;&#x1F4BB;::ADD_TOKEN_ACTIONS;[&TokenId][/rbutton]
            [rbutton]&#x1F6A6;::ADD_TURNORDER;[&TokenId][/rbutton]
            [rbutton]&#x1F7E2;::ADD_AURA;[&TokenId]\1[/rbutton]
            [rbutton]&#x1F7E8;::ADD_AURA;[&TokenId]\0[/rbutton]
          [/td][/tr]
        
    --C[*[&TokenId]:t-layer]|objects:OBJECTS|gmlayer:GM|map:MAP|walls:WALLS

    --&OtherTbl|+ [&t] --&OtherRows|T --^CONTINUE|
    --:OBJECTS| --&ObjTbl|+ [&t] --&ObjRows|T --^CONTINUE|
    --:GM| --&GMTbl|+ [&t] --&GMRows|T --^CONTINUE|
    --:MAP| --&MapTbl|+ [&t] --&MapRows|T --^CONTINUE|
    --:WALLS| --&WallsTbl|+ [&t] --&WallsRows|T --^CONTINUE|

    --:CONTINUE|

    --?[$Rng.Text] -eq TO|[
      --/|TO_GETNEXTITEM populates the next TokenID and increments gTO_ITEM_NDX. gTO_ITEM_NDX is set to 0 if EOD  
      -->TO_GETNEXTITEM|
      --?[$gTO_ITEM_NDX] -eq 0|ENDLOOP
      --&TokenId|[&gTO_ITEM_TOKENID]
      --/| *NextRec|[&gTO_ITEM_TYPE] - [&gTO_ITEM_NAME] - [&gTO_ITEM_POS]
      --?[&gTO_ITEM_TYPE] -ne NPC|CONTINUE|START
    --]|[
      --~TokenId|array;getnext;AllTokens
      --?[&TokenId] -ne ArrayError|LOOPCHECK
    --]|

  --:ENDLOOP|

  --?"[&ObjRows]" -eq "F"|NEXT1
    --&ObjTbl|[tr [&trStyle1]][td [&tdStyle3]]Ojbect Layer NPCs[/td][/tr] [&ObjTbl]
  --:NEXT1|
  --?"[&GMRows]" -eq "F"|NEXT2
    --&GMTbl|[tr [&trStyle1]][td [&tdStyle3]]GM Layer NPCs[/td][/tr] [&GMTbl]
  --:NEXT2|
  --?"[&MapRows]" -eq "F"|NEXT3
    --&MapTbl|[tr [&trStyle1]][td [&tdStyle3]]Map Layer NPCs[/td][/tr] [&MapTbl]
  --:NEXT3|
  --?"[&WallsRows]" -eq "F"|NEXT4
    --&WallsTbl|[tr [&trStyle1]][td [&tdStyle3]]Wall/Lighting Layer NPCs[/td][/tr] [&WallsTbl]
  --:NEXT4|
  --?"[&OtherRows]" -eq "F"|NEXT5
    --&OtherTbl|[tr [&trStyle1]][td [&tdStyle3]]GM Layer NPCs[/td][/tr] [&OtherTbl]
  --:NEXT5|

  --&tbl|[t [&tStyle]] [&ObjTbl] [&GMTbl] [&MapTbl] [&WallsTbl] [&OtherTbl] [/t]
  --+|[&tbl]
  -->FOOTER_BUTTONS_MAIN|  
  --X|

--/|==================  ReEntry Function ========================

--:GMNOTE|TokenID, Mode/Param
  --~Arg|string;split;\;[&reentryval]
  --C[&Arg2]|Token:&Param;token|GMNote:&Param;charnote|Bio:&Param;bio|Avatar:&Param;avatar _notitle|Images:&Param;images _notitle|Config:&Param;config|Help:&Param;help
  --+|[&Param] / [&Arg1]
  --@gmnote|_[&Param] _id[&Arg1]
  --X|
--<|

--:ADD_TOKEN_ACTIONS|TokenID
  --~Arg|string;split;\;[&reentryval]
  --@ta| ({& select [&TokenId]})
  --X|
--<|

--/|==================  NPC Details Report =======================
--:NPC_DETAILS_REPORT| NDR_
  --~Arg|string;split;\;[&reentryval]
  --&CharId|[&Arg1]
  --&TokenId|[&Arg2]
  --#hidecard|0

  --#oddRowBackground|[&BColor1]
  --#evenRowBackground|[&BColor1]
  --#buttonbackground|[&BColor1]
  --#buttontextcolor|[&FColor2]
  --#buttonbordercolor|[&BColor1]

  --#title|[*[&TokenId]:t-name]
  --#leftsub|[*[&CharId]:npc_name]

  --/+|[b][c][*[&CharId]:race][/c][/b]
  --+|[c][i][*[&CharId]:npc_type][/i][/c]

  -->DNDBEYOND_MONSTER_LINK|[*[&CharId]:npc_name];D&DBeyond 
  -->ROLL20_CHARACTERSHEET_LINK|[&CharId];Roll20
  --+Links:|[l]&nbsp;[&ROLL20_CHARACTERSHEET_BTN]&nbsp;&nbsp;&nbsp;&nbsp;[&DNDBEYOND_MONSTER_BTN]&nbsp;&nbsp;[/l]
  --+CharId:|[&CharId]
  --+TokenId:|[&TokenId]

  --?"[*[&CharId]:npc_str_save_flag]" -ne "0"|&Tmp;[*[&CharId]:npc_str_save_base]|&Tmp;[*[&CharId]:strength_save_bonus]  -->ADD_POS_SIGN|[&Tmp] --&StrSave|[&zRET]  --/&Tmp|
  --?"[*[&CharId]:npc_dex_save_flag]" -ne "0"|&Tmp;[*[&CharId]:npc_dex_save_base]|&Tmp;[*[&CharId]:dexterity_save_bonus] -->ADD_POS_SIGN|[&Tmp] --&DexSave|[&zRET] --&Tmp|
  --?"[*[&CharId]:npc_con_save_flag]" -ne "0"|&Tmp;[*[&CharId]:npc_con_save_base]|&Tmp;[*[&CharId]:constitution_save_bonus] -->ADD_POS_SIGN|[&Tmp] --&ConSave|[&zRET] --&Tmp|
  --?"[*[&CharId]:npc_int_save_flag]" -ne "0"|&Tmp;[*[&CharId]:npc_int_save_base]|&Tmp;[*[&CharId]:intelligence_save_bonus] -->ADD_POS_SIGN|[&Tmp] --&IntSave|[&zRET] --&Tmp|
  --?"[*[&CharId]:npc_wis_save_flag]" -ne "0"|&Tmp;[*[&CharId]:npc_wis_save_base]|&Tmp;[*[&CharId]:wisdom_save_bonus] -->ADD_POS_SIGN|[&Tmp] --&WisSave|[&zRET] --&Tmp|
  --?"[*[&CharId]:npc_cha_save_flag]" -ne "0"|&Tmp;[*[&CharId]:npc_cha_save_base]|&Tmp;[*[&CharId]:charisma_save_bonus] -->ADD_POS_SIGN|[&Tmp] --&ChaSave|[&zRET] --&Tmp|

  -->ADD_POS_SIGN|[*[&CharId]:strength_mod] --&StrMod|[&zRET] 
  -->ADD_POS_SIGN|[*[&CharId]:dexterity_mod] --&DexMod|[&zRET]
  -->ADD_POS_SIGN|[*[&CharId]:constitution_mod] --&ConMod|[&zRET]
  -->ADD_POS_SIGN|[*[&CharId]:intelligence_mod] --&IntMod|[&zRET]
  -->ADD_POS_SIGN|[*[&CharId]:wisdom_mod] --&WisMod|[&zRET] 
  -->ADD_POS_SIGN|[*[&CharId]:charisma_mod] --&ChaMod|[&zRET] 

  --&tStyle|style="border:1px solid black;width:100%"
  --&trStyle1|style="background-color:[&BColor1]"
  --&trStyle2|style="background-color:[&BColor1]"
  --&tdStyle1|style="width:25%;text-align:right;font-weight:bold; color: [&FColor1];"
  --&tdStyle2|style="width:25%;text-align:left;font-size:smaller; color: [&FColor2]"
  --&tdStyle3|style="width:25%;text-align:right;font-weight:bold; color: [&FColor1];" 
  --&tdStyle4|style="width:25%;text-align:center;font-weight:bold; color: [&FColor2]"

  --+|[t [&tStyle]]
        [tr [&trStyle1]][td [&tdStyle1]][u]Attr[/u][/td]  [td style="width:50%;text-align:center;font-weight:bold" colspan=2] [u]Score[/u][/td]           [td [&tdStyle4]][u]Save[/u][/td][/tr]
        [tr [&trStyle2]][td [&tdStyle1]]Str[/td]          [td [&tdStyle3]] [&StrMod][/td] [td [&tdStyle2]]&#40;[*[&CharId]:strength]&#41; [/td] [td [&tdStyle4]][button][&StrSave]::~[*[&CharId]:character_name]|npc_str_save[/button][/td][/tr]
        [tr [&trStyle1]][td [&tdStyle1]]Dex[/td]          [td [&tdStyle3]] [&DexMod][/td] [td [&tdStyle2]]&#40;[*[&CharId]:dexterity]&#41;  [/td] [td [&tdStyle4]][button][&DexSave]::~[*[&CharId]:character_name]|npc_dex_save[/button][/td][/tr]
        [tr [&trStyle2]][td [&tdStyle1]]Con[/td]          [td [&tdStyle3]] [&ConMod][/td] [td [&tdStyle2]]&#40;[*[&CharId]:constitution]&#41; [/td] [td [&tdStyle4]][button][&ConSave]::~[*[&CharId]:character_name]|npc_con_save[/button][/td][/tr]
        [tr [&trStyle1]][td [&tdStyle1]]Int[/td]          [td [&tdStyle3]] [&IntMod][/td] [td [&tdStyle2]]&#40;[*[&CharId]:intelligence]&#41; [/td] [td [&tdStyle4]][button][&IntSave]::~[*[&CharId]:character_name]|npc_int_save[/button][/td][/tr]
        [tr [&trStyle2]][td [&tdStyle1]]Wis[/td]          [td [&tdStyle3]] [&WisMod][/td] [td [&tdStyle2]]&#40;[*[&CharId]:wisdom]&#41; [/td][td [&tdStyle4]][button][&WisSave]::~[*[&CharId]:character_name]|npc_wis_save[/button][/td][/tr]
        [tr [&trStyle1]][td [&tdStyle1]]Cha[/td]          [td [&tdStyle3]] [&ChaMod][/td] [td [&tdStyle2]]&#40;[*[&CharId]:charisma]&#41; [/td][td [&tdStyle4]][button][&ChaSave]::~[*[&CharId]:character_name]|npc_cha_save[/button][/td][/tr]
      [/t]

  --&SCA|[*[&CharId]:spellcasting_ability]
  --~SCA|string;substring;3;3;[&SCA]

  -->ADD_POS_SIGN|[*[&CharId]:initiative_bonus] --&InitBonus|[&zRET] 
  -->ADD_POS_SIGN|[*[&CharId]:pb] --&PB|[&zRET] 
  --&PWis|[*[&CharId]:passive_wisdom]

  -->ADD_POS_SIGN|[*[&CharId]:spell_attack_bonus] --&SAB|[&zRET] 
  -->ADD_POS_SIGN|[*[&CharId]:spell_save_dc] --&SSDC|[&zRET] 

  --/|4 column table /w desc:pair every 2 columns
  --&tStyle|style="border:0px solid black;width:100%"
  --&trStyle1|style="background-color:[&BColor1]"
  --&trStyle2|style="background-color:[&BColor1]"

  --&tdStyle1|style="width:35%;text-align:right;vertical-align:top;color: [&FColor1];"
  --&tdStyle2|style="width:10%;text-align:left;vertical-align:top;color: [&FColor2]"
  --&tdStyle3|colspan=3 style="width:70%;text-align:left;vertical-align:top;color: [&FColor1];"

  --&tdStyle4|style="width:20%;text-align:right;vertical-align:top;color: [&FColor2]"
  --&tdStyle5|style="width:35%;text-align:left;vertical-align:top;color: [&FColor2]"


  --+|[t [&tStyle]]
        [tr [&trStyle1]] [td [&tdStyle1]][b]AC:[/b] [/td][td [&tdStyle2]]&nbsp;[*[&CharId]:npc_ac] [/td]
            [td [&tdStyle4]][b]HP:[/b] [/td][td [&tdStyle5]]&nbsp;[*[&TokenId]:t-bar1_value]/[*[&TokenId]:t-bar1_max][/td][/tr]
        [tr [&trStyle2]][td [&tdStyle1]][b]AC Type:[/b] [/td][td [&tdStyle3]]&nbsp;[*[&CharId]:npc_actype][/td][/tr]
        [tr [&trStyle1]][td [&tdStyle1]][b]Speed:[/b] [/td][td [&tdStyle3]] &nbsp;[*[&CharId]:npc_speed][/td][/tr]
        [tr [&trStyle2]][td [&tdStyle1]][b]Initiative:[/b] [/td][td [&tdStyle2]]&nbsp;[&InitBonus][/td]
            [td [&tdStyle4]][b]PWis:[/b] [/td][td [&tdStyle5]]&nbsp;[&PWis][/td][/tr]
        [tr [&trStyle1]][td [&tdStyle1]][b]Prof Bonus:[/b] [/td][td [&tdStyle2]]&nbsp;[&PB][/td] [td [&tdStyle4]][/td][td [&tdStyle5]] [/td][/tr]
        [tr [&trStyle1]][td [&tdStyle1]][b]Spell Save:[/b] [/td][td [&tdStyle2]]&nbsp;[&SSDC] ([&SCA])[/td]
            [td [&tdStyle4]][b]Attack:[/b]&nbsp;[/td][td [&tdStyle5]] [&SAB][/td][/tr]
        [tr [&trStyle2]][td [&tdStyle1]][b]Vulnerable:[/b] [/td][td [&tdStyle3]]&nbsp;[*[&CharId]:npc_vulnerabilities][/td][/tr]
        [tr [&trStyle1]][td [&tdStyle1]][b]Dmg Resist:[/b] [/td][td [&tdStyle3]]&nbsp;[*[&CharId]:npc_resistances][/td][/tr]
        [tr [&trStyle2]][td [&tdStyle1]][b]Dmg Immune:[/b] [/td][td [&tdStyle3]]&nbsp;[*[&CharId]:npc_immunities][/td][/tr]
        [tr [&trStyle1]][td [&tdStyle1]][b]Cond Immune:[/b] [/td][td [&tdStyle3]]&nbsp;[*[&CharId]:npc_condition_immunities][/td][/tr]
        [tr [&trStyle2]][td [&tdStyle1]][b]Languages:[/b] [/td][td [&tdStyle3]]&nbsp;[*[&CharId]:npc_languages][/td][/tr]
        [tr [&trStyle1]][td [&tdStyle1]][b]Senses:[/b] [/td][td [&tdStyle3]]&nbsp;[*[&CharId]:npc_senses][/td][/tr]
      [/t]

  --&tStyle|style="border:1px solid black;width:100%"
  --&trStyle1|style="background-color:[&BColor1]"
  --&trStyle2|style="background-color:[&BColor1]"
  --&tdStyle1|style="width:35%;text-align:left;font-weight:bold;color: [&FColor1];"
  --&tdStyle2|style="width:15%;text-align:center;font-weight:bold;color: [&FColor2]"
  --&tdStyle3|colspan=4 style="width:100%;text-align:center;color: [&FColor1];"

  --?"[*[&CharId]:npc_acrobatics_flag]" -ne "0"|&Tmp;[*[&CharId]:npc_acrobatics_base]|&Tmp;[*[&CharId]:acrobatics_bonus] -->ADD_POS_SIGN|[&Tmp] --&AcrobaticsBonus|[&zRET]
  --?"[*[&CharId]:npc_animal_handling_flag]" -ne "0"|&Tmp;[*[&CharId]:npc_animal_handling_base]|&Tmp;[*[&CharId]:animal_handling_bonus] -->ADD_POS_SIGN|[&Tmp] --&AnimalHandlingBonus|[&zRET]
  --&tbl|[t [&tStyle]]
        [tr [&trStyle1]][td [&tdStyle1]]Acrobatics[/td]
            [td [&tdStyle2]] [b][button][&AcrobaticsBonus]::~[*[&CharId]:character_name]|npc_acrobatics[/button][/b][/td]
            [td [&tdStyle1]]Animal Hand[/td]
            [td [&tdStyle2]][b][button][&AnimalHandlingBonus]::~[*[&CharId]:character_name]|npc_animal_handling[/button][/b][/td][/tr]

  --?"[*[&CharId]:npc_arcana_flag]" -ne "0"|&Tmp;[*[&CharId]:npc_arcana_base]|&Tmp;[*[&CharId]:arcana_bonus] -->ADD_POS_SIGN|[&Tmp] --&ArcanaBonus|[&zRET]
  --?"[*[&CharId]:npc_athletics_flag]" -ne "0"|&Tmp;[*[&CharId]:npc_athletics_base]|&Tmp;[*[&CharId]:athletics_bonus] -->ADD_POS_SIGN|[&Tmp] --&AthleticsBonus|[&zRET]
  --&tbl|+ [tr [&trStyle2]][td [&tdStyle1]]Arcana[/td]
            [td [&tdStyle2]][button][&ArcanaBonus]::~[*[&CharId]:character_name]|npc_arcana[/button][/td]
            [td [&tdStyle1]]Athletics[/td]
            [td [&tdStyle2]][button][&AthleticsBonus]::~[*[&CharId]:character_name]|npc_athletics[/button][/td][/tr]

  --?"[*[&CharId]:npc_deception_flag]" -ne "0"|&Tmp;[*[&CharId]:npc_deception_base]|&Tmp;[*[&CharId]:deception_bonus] -->ADD_POS_SIGN|[&Tmp] --&DeceptionBonus|[&zRET]
  --?"[*[&CharId]:npc_history_flag]" -ne "0"|&Tmp;[*[&CharId]:npc_history_base]|&Tmp;[*[&CharId]:history_bonus] -->ADD_POS_SIGN|[&Tmp] --&HistoryBonus|[&zRET]
  --&tbl|+ [tr [&trStyle1]][td [&tdStyle1]]Deception[/td]
            [td [&tdStyle2]][button][&DeceptionBonus]::~[*[&CharId]:character_name]|npc_deception[/button][/td]
            [td [&tdStyle1]]History[/td]
            [td [&tdStyle2]][button][&HistoryBonus]::~[*[&CharId]:character_name]|npc_history[/button][/td][/tr]

  --?"[*[&CharId]:npc_insight_flag]" -ne "0"|&Tmp;[*[&CharId]:npc_insight_base]|&Tmp;[*[&CharId]:insight_bonus] -->ADD_POS_SIGN|[&Tmp] --&InsightBonus|[&zRET]
  --?"[*[&CharId]:npc_intimidation_flag]" -ne "0"|&Tmp;[*[&CharId]:npc_intimidation_base]|&Tmp;[*[&CharId]:intimidation_bonus] -->ADD_POS_SIGN|[&Tmp] --&IntimidationBonus|[&zRET]
  --&tbl|+ [tr [&trStyle1]][td [&tdStyle1]]Insight[/td]
            [td [&tdStyle2]][button][&InsightBonus]::~[*[&CharId]:character_name]|npc_insight[/button][/td]
            [td [&tdStyle1]]Intimidation[/td]
            [td [&tdStyle2]][button][&IntimidationBonus]::~[*[&CharId]:character_name]|npc_intimidation[/button][/td][/tr]

  --?"[*[&CharId]:npc_investigation_flag]" -ne "0"|&Tmp;[*[&CharId]:npc_investigation_base]|&Tmp;[*[&CharId]:investigation_bonus] -->ADD_POS_SIGN|[&Tmp] --&InvestigationBonus|[&zRET]
  --?"[*[&CharId]:npc_medicine_flag]" -ne "0"|&Tmp;[*[&CharId]:npc_medicine_base]|&Tmp;[*[&CharId]:medicine_bonus] -->ADD_POS_SIGN|[&Tmp] --&MedicineBonus|[&zRET]
  --&tbl|+ [tr [&trStyle1]][td [&tdStyle1]]Investigation[/td]
            [td [&tdStyle2]][button][&InvestigationBonus]::~[*[&CharId]:character_name]|npc_investigation[/button][/td]
            [td [&tdStyle1]]Medicine[/td]
            [td [&tdStyle2]][button][&MedicineBonus]::~[*[&CharId]:character_name]|npc_medicine[/button][/td][/tr]

  --?"[*[&CharId]:npc_nature_flag]" -ne "0"|&Tmp;[*[&CharId]:npc_nature_base]|&Tmp;[*[&CharId]:nature_bonus] -->ADD_POS_SIGN|[&Tmp] --&NatureBonus|[&zRET]
  --?"[*[&CharId]:npc_perception_flag]" -ne "0"|&Tmp;[*[&CharId]:npc_perception_base]|&Tmp;[*[&CharId]:perception_bonus] -->ADD_POS_SIGN|[&Tmp] --&PerceptionBonus|[&zRET]
  --&tbl|+ [tr [&trStyle1]][td [&tdStyle1]]Nature[/td]
            [td [&tdStyle2]][button][&NatureBonus]::~[*[&CharId]:character_name]|npc_nature[/button][/td]
            [td [&tdStyle1]]Perception[/td]
            [td [&tdStyle2]][button][&PerceptionBonus]::~[*[&CharId]:character_name]|npc_perception[/button][/td][/tr]

  --?"[*[&CharId]:npc_performance_flag]" -ne "0"|&Tmp;[*[&CharId]:npc_performance_base]|&Tmp;[*[&CharId]:performance_bonus] -->ADD_POS_SIGN|[&Tmp] --&PerformanceBonus|[&zRET]
  --?"[*[&CharId]:npc_persuasion_flag]" -ne "0"|&Tmp;[*[&CharId]:npc_persuasion_base]|&Tmp;[*[&CharId]:persuasion_bonus] -->ADD_POS_SIGN|[&Tmp] --&PersuasionBonus|[&zRET]
  --&tbl|+ [tr [&trStyle1]][td [&tdStyle1]]Performance[/td]
            [td [&tdStyle2]][button][&PerformanceBonus]::~[*[&CharId]:character_name]|npc_performance[/button][/td]
            [td [&tdStyle1]]Persuasion[/td]
            [td [&tdStyle2]][button][&PersuasionBonus]::~[*[&CharId]:character_name]|npc_persuasion[/button][/td][/tr]

  --?"[*[&CharId]:npc_religion_flag]" -ne "0"|&Tmp;[*[&CharId]:npc_religion_base]|&Tmp;[*[&CharId]:religion_bonus] -->ADD_POS_SIGN|[&Tmp] --&ReligionBonus|[&zRET]
  --?"[*[&CharId]:npc_sleight_of_hand_flag]" -ne "0"|&Tmp;[*[&CharId]:npc_sleight_of_hand_base]|&Tmp;[*[&CharId]:sleight_of_hand_bonus] -->ADD_POS_SIGN|[&Tmp] --&SOHBonus|[&zRET]
  --&tbl|+ [tr [&trStyle1]][td [&tdStyle1]]Religion[/td]
            [td [&tdStyle2]][button][&ReligionBonus]::~[*[&CharId]:character_name]|npc_religion[/button][/td]
            [td [&tdStyle1]]SoH[/td]
            [td [&tdStyle2]][button][&SOHBonus]::~[*[&CharId]:character_name]|npc_sleight_of_hand[/button][/td][/tr]

  --?"[*[&CharId]:npc_stealth_flag]" -ne "0"|&Tmp;[*[&CharId]:npc_stealth_base]|&Tmp;[*[&CharId]:stealth_bonus] -->ADD_POS_SIGN|[&Tmp] --&StealthBonus|[&zRET]
  --?"[*[&CharId]:npc_survival_flag]" -ne "0"|&Tmp;[*[&CharId]:npc_survival_base]|&Tmp;[*[&CharId]:survival_bonus] -->ADD_POS_SIGN|[&Tmp] --&SurvivalBonus|[&zRET]

  --&tbl|+ [tr [&trStyle1]][td [&tdStyle1]]Stealth[/td]
            [td [&tdStyle2]][button][&StealthBonus]::~[*[&CharId]:character_name]|npc_stealth[/button][/td]
            [td [&tdStyle1]]Survival[/td]
            [td [&tdStyle2]][button][&SurvivalBonus]::~[*[&CharId]:character_name]|npc_survival[/button][/td][/tr]
  --+|[&tbl]

  -->SECTION_HEADER|Quick Note
  --+|[rbutton][+]::ADD_QUICK_NOTE;[&TokenId][/rbutton][*[&TokenId]:t-bar3_value]

  -->FOOTER_BUTTONS_SECONDARY| Add Top Button to bottom right corner of card
  --X|

  --:NDR_ADDGM| 
    --&tbl|+ [tr][td style="text-align:left"][%1%][/td] [td style="text-align:right"]([%2%])[/td][/tr]
  --<|

--:ADD_QUICK_NOTE|TokenId
  --#hidecard|1
  --~Arg|string;split;\;[&reentryval]
  --&TokenId|[&Arg1]
  --I;Add a quick note to [*[&TokenId]:t-name]|q;QN;Add a QuickNote?

  --@token-mod|_set bar3_value|"[&QN]" _ids [&TokenId] _ignore-selected
--X|

--:ADD_AURA|TokenId, ShowPlayers(1 is on, 2 is off)
  --#hidecard|1
  --~Arg|string;split;\;[&reentryval]
  --&TokenId|[&Arg1]
  --&ShowPlayers|[&Arg2]
  
  --&Color1|#ffff00
  --&Color2|#66ff66
  --&Color3|#cc0000
  --&Color|[&Color1]
 
  --?[&ShowPlayers] -eq 1|[
    --&Aura|1
    --&Radius|5
  --]|[
    --&Aura|2
    --&Radius|5
  --]|


  --/|*AA1|[&ShowPlayers]/[&Aura]/[&Radius] [*[&TokenId]:t-aura[&Aura]_radius] [*[&TokenId]:t-aura[&Aura]_color]

  --?[*[&TokenId]:t-aura[&Aura]_radius] -gt 1|ADD_AURA_ROTATE_COLORS
    --&Color|[&Color1]
    --^ADD_AURA_APPLY|
  --:ADD_AURA_ROTATE_COLORS|
    --?[*[&TokenId]:t-aura[&Aura]_color] -eq [&Color1]|&Color;[&Color2]
    --?[*[&TokenId]:t-aura[&Aura]_color] -eq [&Color2]|&Color;[&Color3]
    --?[*[&TokenId]:t-aura[&Aura]_color] -eq [&Color3]|&Radius;0
  --:ADD_AURA_APPLY|

  --/|*AA2|[&Radius] [&Color]

  --?[&Radius] -ne 0|ADD_AURA_RADIUS_ON|ADD_AURA_RADIUS_OFF
  --:ADD_AURA_RADIUS_ON|
    --@token-mod|_set aura[&Aura]_radius|[&Radius] aura[&Aura]_color|[&Color] _on showplayers_aura1 aura2_square _off showplayers_aura2 aura1_square _ids [&TokenId] _ignore-selected
    --X|
  --:ADD_AURA_RADIUS_OFF|
    --@token-mod|_set aura[&Aura]_radius|  _on showplayers_aura1 aura2_square _off showplayers_aura2 aura1_square _ids [&TokenId] _ignore-selected
--X|

--/|==================  NPC Features/Actions Report =======================
--:NPC_FEATURES_REPORT| NFR_
  --~Arg|string;split;\;[&reentryval]
  --&CharId|[&Arg1]
  --&TokenId|[&Arg2]
  --#hidecard|0
  --#title|[*[&TokenId]:t-name]
  --#leftsub|NPC Features/Actions

  --#oddRowBackground|[&BColor1]
  --#evenRowBackground|[&BColor1]
  --#buttonbackground|[&BColor1]
  --#buttontextcolor|[&FColor1]
  --#buttonbordercolor|[&BColor1]

  --/#oddRowBackground|#edf7f0
  --/#evenRowBackground|#edf7f0
  --/#buttonbackground|#edf7f0
  --/#buttontextcolor|#095c1f
  --/#buttonbordercolor|#edf7f0  

  --/| Traits
  --&Header|Traits
  -->Lib5E_NPC_Description_List|[&CharId];repeating_npctrait;Traits;[&Header]

  --/| Actions/Attacks
  --&Header|Actions/Attacks
  -->Lib5E_NPC_Action_List|[&CharId];[&Header]

  --/| Legendary Actions
  --&Header|Legendary Actions
  -->Lib5E_NPC_Legendary_Action_List|[&CharId];[&Header] 

  --/| Reactions
  --&Header|Reactions
  -->Lib5E_NPC_Description_List|[&CharId];repeating_npcreaction;Available Reactions;[&Header]

  --/| Spells
  --&Header|Spells


  -->FOOTER_BUTTONS_SECONDARY| Add Top Button to bottom right corner of card
  --X|

--/|==================  Spells Report =======================
--:NPC_SPELLS_REPORT| NSR_ 
  --~Arg|string;split;\;[&reentryval]
  --&CharId|[&Arg1]
  --&TokenId|[&Arg2]
  --#hidecard|0
  --#buttontextcolor|[&FColor1]
  --#buttonbordercolor[&BColor1]
  --#buttonbackground||[&BColor1]

  --#oddRowBackground|[&BColor1]
  --#evenRowBackground|[&BColor1]
  
  --#title|[*[&TokenId]:t-name]
  --#leftsub|NPC Spells

  --&tStyle|style="width:100%;text-align:left;padding:1px;border-spacing:0px;border-collapse:collapse;text-shadow: 0px 0px 0px black;border: 0px dashed black;"
  --&trStyle1|style="border:1px dashed black;"
  --&trStyle2|style="border:0px dashed black;"
  --&tdStyle1|style="width:20%;text-align:right"
  --&tdStyle2|style="width:20%;text-align:center"
  --&tdStyle3|style="width:100%;text-align:center;background-color:[&BColor2];font-size:100%" colspan=2  

  --/| Spell Attributes
  --&SCA|[*[&CharId]:spellcasting_ability]
  --~SCA|string;substring;3;3;[&SCA]

  --+|[t [&tStyle]]
        [tr][td [&tdStyle1]][b]Save:[/b] [/td][td [&tdStyle2]][*[&CharId]:spell_save_dc] ([&SCA])[/td]
            [td [&tdStyle1]][b]Spell Atk:[/b] [/td][td [&tdStyle2]][*[&CharId]:spell_attack_bonus][/td]
            [td [&tdStyle2]][rbutton]Reset::RESET_SPELL_SLOTS;[&CharId][/rbutton][/td]
            [/tr][/t]

  -->REPORTSPELLS|cantrip
  -->REPORTSPELLS|1
  -->REPORTSPELLS|2
  -->REPORTSPELLS|3
  -->REPORTSPELLS|4
  -->REPORTSPELLS|5
  -->REPORTSPELLS|6
  -->REPORTSPELLS|7
  -->REPORTSPELLS|8
  -->REPORTSPELLS|9

  -->FOOTER_BUTTONS_SECONDARY| Add Top Button to bottom right corner of card

  --X|

--:REPORTSPELLS|Parameter: Level
  --#hidecard|0
  --&zLvl|[%1%]

  --Rfirst|[*[&CharId]:character_id];repeating_spell-[&zLvl]

  --?"[*R:spellname]" -eq NoRepeatingAttributeLoaded|SB_DONE
  
  --&STblStyle1|"width:100%;text-align:center;padding:5px;border-spacing:0px;border-collapse:collapse;border: 1px dashed purple;"  
  
  --&LvlDesc|Cantrip
  --&zLvlSlots|

  --?[&zLvl] -eq cantrip|SB_SKIPCANTRIP
    --&LvlDesc|Level [&zLvl]
    --=SlotsTotal|[*[&CharId]:lvl[&zLvl]_slots_total]
    --=SlotsExpended|[*[&CharId]:lvl[&zLvl]_slots_expended]
    --&zLvlSlots|Slots [b][$SlotsExpended.Total][/b] of [b][$SlotsTotal.Total][/b] remaining
  --:SB_SKIPCANTRIP|

  --&STbl|[t style=[&STblStyle1]][tr][td][&LvlDesc][/td][td][&zLvlSlots][/td][/tr][/t]
  --+|[&STbl]

  --:SB_DISPLAYLOOP|
    --?"[*R:spellname]" -eq NoRepeatingAttributeLoaded|SB_DONE

      --&zRIT|[r]
      --?"[*R:spellritual]" -inc "Yes"|SB_ENDRIT
        --&zRIT|
      --:SB_ENDRIT|

      --&zCON|[c]
      --?"[*R:spellconcentration]" -inc "concentration=1"|SB_ENDCON
        --&zCON|
      --:SB_ENDCON| 

      --?[&zLvl] -eq cantrip -or [*R:spellprepared] -eq 1|SB_PREPARED
        --+|[rbutton]&#x2B1C;::TOGGLE_PREPARED_SPELL;[&CharId]\[*R>spellprepared]\1[/rbutton] [rbutton]&#x1F50E;[*R:spellname]::SPELL_DETAILS;[*R:spellname]\repeating_spell-[&zLvl]\spellname[/rbutton] [i][*R:innate] [&zCON][&zRIT][/i]
      --^SB_ENDIF-1|

      --:SB_PREPARED|
        --+|[rbutton]&#x2705;::TOGGLE_PREPARED_SPELL;[&CharId]\[*R>spellprepared]\0[/rbutton][rbutton]&#x1F50E;[*R:spellname]::SPELL_DETAILS;[*R:spellname]\repeating_spell-[&zLvl]\spellname[/rbutton] [i][*R:innate] [&zCON][&zRIT][/i]
      --:SB_ENDIF-1|
    --Rnext|

  --^SB_DISPLAYLOOP|  
  --:SB_DONE|

--<|Return

--/|==================  Spell Details =======================
--:SPELL_DETAILS| (SBD_)
  --/+ReEntryVal|[&reentryval]
  --~rArgs|string;split;\;[&reentryval]
  --/+Split|[&rArgs]: [&rArgs1], [&rArgs2], [&rArgs3]

  --Rfind|[*[&CharId]:character_id];[&rArgs1];[&rArgs2];[&rArgs3]
  --/Rdump|

  --#hidecard|0
  --#title|[*R:spellname]  [*R:innate]
  --#leftsub|[*[&CharId]:character_name] ([*[&CharId]:caster_level])
  --#rightsub|[*R:spellschool] [*R:spelllevel]
  --#oddRowBackground|[&BColor1]
  --#evenRowBackground|[&BColor1]
  --#buttonbackground|[&BColor1]
  --#buttontextcolor|[&FColor1]
  --#buttonbordercolor|[&BColor1]


  --+Casting Time:|[*R:spellcastingtime]
  --+Range:|[*R:spellrange]
  --+Target:|[*R:spelltarget]

  --&zSCV|V
  --?"[*R:spellcomp_v]" -inc "v=1"|SBD_ENDSCV
    --&zSCV|
  --:SBD_ENDSCV|

  --&zSCS|S
  --?"[*R:spellcomp_s]" -inc "s=1"|SBD_ENDSCS
    --&zSCS|
  --:SBD_ENDSCS|

  --&zSCM|M
  --?"[*R:spellcomp_m]" -inc "m=1"|SBD_ENDSCM
    --&zSCM|
  --:SBD_ENDSCM|

  --+Components:|[&zSCV][&zSCS][&zSCM] [i]([*R:spellcomp_materials])[/i]
  --+Duration:|[*R:spellduration]
  --+|[*R:spelldescription]
  
  --~Len|string;length;[*R:spellathigherlevels]
  --?[$Len] -le 5|SBD_END_HV_CHECK
    --+|[b][i]At Higher Levels.[/i][/b][*R:spellathigherlevels]
  --:SBD_END_HV_CHECK|
  
   --+|[c][sheetbutton]&#x1F4AB; Cast Spell &#x1F9D9;::[*[&CharId]:character_name]::[&rArgs2]_[*R:xxxActionIDxxxx]_spell[/sheetbutton][/c]

  -->DNDBEYOND_SPELL_LINK|[*R:spellname]; D&DBeyond
  --+|[l][b]Spell Save DC[/b] [*[&CharId]:spell_save_dc][/l][r][&DNDBEYOND_SPELL_BTN][/r]

  -->FOOTER_BUTTONS_SECONDARY| Add Top Button to bottom right corner of card
  
  --X|
--<|

--/|==================  Toggle Spell Prepared =======================
--:TOGGLE_PREPARED_SPELL|CharId; SpellPrepared_field
  --#hidecard|1
  --+ReEntryVal|[&reentryval]
  --~rArgs|string;split;\;[&reentryval]
  --+Split|[&rArgs]: [&rArgs1], [&rArgs2], [&rArgs3]
  --@setattr|_charid [&rArgs1] _evaluate _silent _[&rArgs2]|[&rArgs3]
  --+Prepared Toggled| @setattr|_charid [&Args1] _evaluate _[&rArgs2]|[&rArgs3]

  --X|
--<|


--/|==================  RESET SPELL Slots =======================
--:RESET_SPELL_SLOTS| Arg1: CharId 
  --/|Rest Spell slots for character sent in 
  --#title|Reset Spell Slots 
  --#whisper|gm
  --#hidecard|0
  --~Arg|string;split;\;[&reentryval]

  --@setattr|_charid [&Arg1] _evaluate _lvl1_slots_expended|%lvl1_slots_total% _lvl2_slots_expended|%lvl2_slots_total% _lvl3_slots_expended|%lvl3_slots_total% _lvl4_slots_expended|%lvl4_slots_total% _lvl5_slots_expended|%lvl5_slots_total% _lvl6_slots_expended|%lvl6_slots_total% _lvl7_slots_expended|%lvl7_slots_total% _lvl8_slots_expended|%lvl8_slots_total% _lvl9_slots_expended|%lvl9_slots_total%
  --X| Exit
--<|

--/|==================  Add Turnorder =======================
--:ADD_TURNORDER| Arg1: TokenID  (AT_)
  --/|Add to TurnOrder (Init Roll)
  --#title|Add to Turnorder
  --#whisper|gm
  --#hidecard|0
  --~Arg|string;split;\;[&reentryval]
  --&TokenId|[&Arg1]

  --#leftsub|[*[&TokenId]:t-name]
  --#rightsub|Bonus: [*[&TokenId]:initiative_bonus]

  --=Init|1d20 + [*[&TokenId]:initiative_bonus]
  --~temp|turnorder;replacetoken;[&TokenId];[$Init]

  --+|[*[&TokenId]:t-name] added to the Turn Order: [$Init]
  --+ImageURL|[img width=50 height=50][*[&TokenId]:t-imgsrc][/img]
  --X| Exit
--<|


--/|==================  Add Numbered to Token Name =======================
--:ADD_NUMBERED| Arg1: TokenID  (AN_)
  --/|Add %%NUMBERED%% to token name for TokenAutoNumber script
  --#title|Add %%NUMBERED%%
  --#whisper|gm
  --#hidecard|0

  --~Arg|string;split;\;[&reentryval]
  --+Debug|[&reentryval]
  --+Debug|[&Arg1]
  --&TokenId|[&Arg1]

  --?"[*[&TokenId]:t-name]" -inc NUMBERED|AN_EXIT

  --/|Token doesnt have %%NUMBERED%%, so lets add it
  --&TN|[*[&TokenId]:t-name] %%NUMBERED%%
  --+Token Name Changed|
  --+From|[r][*[&TokenId]:t-name][/r]
  --+To|[r][&TN][/r]
  --@token-mod|_set name|"[&TN]"" _ids [&TokenId] _ignore-selected

  --X| Exit
  --:AN_EXIT|
  --+Token Name Already has required syntax|
  --X| Exit
--<|

--:TOGGLE_LAYER|TokenId
  --#hidecard|1
  --~Arg|string;split;\;[&reentryval]
  --+TOGGLE_LAYER |[&Arg1]
  --&TokenId|[&Arg1]

  --?"[*[&TokenId]:t-layer]" -eq "objects"|&Layer;gmlayer|&Layer;objects

  -->SET_LAYER|[&TokenId];[&Layer]
--<|



--/|==================  Utility Functions =======================
--:SHORT_NAME|Shortens player name for reporting purposes (6 characters); Parameter:Character_Name
  --~gSN|string;left;6;[%1%]
--<| Return

--:SET_LAYER|TokenId;layer
  --+SET_LAYER |[%1%] [%2%]
  --&TokenId|[%1%]
  --&Layer|[%2%]
  --@token-mod|_set layer|[&Layer] _ids [&TokenId] _ignore-selected

  --X|
--<|

--:FIND_TOKEN|TokenId - Pings the token name
  --#hidecard|1  
  --/@find-token|_[&reentryval]
  --@ping-token|_[&reentryval]
  --X|
--<|

--:IS_TOKEN_IN_RANGE|Anchor Token, Checked Token, Range (in feet)
  --&AnchorToken|[%1%]
  --&TestToken|[%2%]
  --=Range|[%3%]
  --&IN_RANGE|0
  --/:CHECK DISTANCE IN ft.|

  --~d|euclideanlong;[&AnchorToken];[&TestToken]
  --?X[*P:snapping_increment] -eq X|&SnapInc;1|&SnapInc;[*P:snapping_increment]  
  --=Scale|[*P:scale_number] --~Scale|math;max;1;[$Scale]
  --=DIST|[$d] / [$SnapInc] * [$Scale] --~DIST|math;round;[$DIST]

  --?[$DIST] -gt [$Range]|&IN_RANGE;0|&IN_RANGE;1
  --<|Return 

--:CONCAT|Add 2 strings
  --&gCONCAT|[%1%][%2%]
  --[%2%]|[&gCONCAT]
  --[%1%]|[&gCONCAT]
--<| Return

--:ADD_POS_SIGN|Parameter1: Number / Can be a roll or string variable, but should be a number. 
 --/| Populates zRET(String) and rRET(Roll Variable) (Global vars effectively)
  --&zRET|[%1%]
  --=rRET|[%1%]
  --?[%1%] -le 0|_APS_SKIP  --=rRET|&#43;[%1%] --&zRET|[$rRET.Text] --:_APS_SKIP|
--<|

--:SECTION_HEADER|Title
  --&hdrstyle_T|style="width:100%;padding:1px;border-spacing:0px;border-collapse:collapse;text-shadow: 0px 0px 0px black;border:1px solid black;"
  --&hdrstyle_TR|style="border:0px solid black;"
  --&hdrstyle_TD|style="width:100%;background-color:#edf7f0;font-size:110%;font-weight:bold;text-align:center"
  --+|[t [&hdrstyle_T]][tr [&hdrstyle_TR]][td [&hdrstyle_TD]][c][%1%][/c][/td][/tr][/t]
--<|

--:FOOTER_BUTTONS_MAIN|
    --+|[r][button]MapNotes::~Mule|MapNote-Tools[/button]
            [button]NPC::~Mule|NPC-Tools[/button]
            [button]DM::~Mule|DM-Tools[/button][/r]  
--<|

--:FOOTER_BUTTONS_SECONDARY|
    --+|[l][rbutton]&#x1F4C3;::NPC_DETAILS_REPORT;[&CharId]\[&TokenId]\[/rbutton]
            [rbutton]&#x1F3F9;::NPC_FEATURES_REPORT;[&CharId]\[&TokenId]\[/rbutton]
            [rbutton]&#x1F4AB;::NPC_SPELLS_REPORT;[&CharId]\[&TokenId]\[/rbutton]
            [rbutton]&#x1F6A6;::ADD_TURNORDER;[&TokenId][/rbutton]
            [rbutton]&#x1F51F;::ADD_NUMBERED;[&TokenId][/rbutton]
            [rbutton]&#x1F3AF;::FIND_TOKEN;[&TokenId][/rbutton][/l]
         [r][button]MapNotes::~Mule|MapNote-Tools[/button]
            [button]NPC::~Mule|NPC-Tools[/button]
            [button]DM::~Mule|DM-Tools[/button][/r]  
    --/+CharId|[&CharId]
    --/+TokenId|[&TokenId]

--<|

--:ROLL20_CHARACTERSHEET_LINK|CharId, button caption
  --&zROLL20_CS|https://journal.roll20.net/character/[%1%]
  --&ROLL20_CHARACTERSHEET_BTN|[button][%2%]::[&zROLL20_CS][/button]
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

--:Lib5E_NPC_Description_List|character_id;repeatingList; header
  --Rfirst|[%1%];[%2%];[%3%]
  --/rDump| 
  --?"[*R:name]" -eq "NoRepeatingAttributeLoaded"|_Lib5E_NPCDESCL_Done
  -->SECTION_HEADER|[%4%]  

  --:_Lib5E_NPCDESCL_Loop| 
    --?"[*R:name]" -eq "NoRepeatingAttributeLoaded"|_Lib5E_NPCDESCL_Done
    --&_Lib5E_NameTemp|[*R:name] 
    --~_Lib5E_NameTemp|string;replace;(; ;[&_Lib5E_NameTemp] 
    --~_Lib5E_NameTemp|string;replace;); ;[&_Lib5E_NameTemp]
    --+|[b][u][&_Lib5E_NameTemp]. [/b][/u][*R:description]
    --+|[br]
    --Rnext| 
  --^_Lib5E_NPCDESCL_Loop| 
  --:_Lib5E_NPCDESCL_Done| 
--<|

--:Lib5E_NPC_Action_List|character_id; header
  --&_Lib5E_Temp| 

  --Rfirst|[%1%];repeating_npcaction 
  --?"[*R:name]" -eq "NoRepeatingAttributeLoaded"|_Lib5E_NPCAL_Done 

  -->SECTION_HEADER|[%2%]  

  --:_Lib5E_NPCAL_Loop| 
    --rDump| 
    --?"[*R:name]" -eq "NoRepeatingAttributeLoaded"|_Lib5E_NPCAL_Done 
    --&_Lib5E_NameTemp|[*R:name] 
    --~_Lib5E_NameTemp|string;replace;(; ;[&_Lib5E_NameTemp] --~_Lib5E_NameTemp|string;replace;); ;[&_Lib5E_NameTemp] 
      --+|[br]
      --+[b][u][&_Lib5E_NameTemp].[/u][/b]| [r][button]&#x1F3F9;::~[%1%]|[*R>npc_action][/button][/r]
    --?"[*R:attack_flag]" -ne "on"|_Lib5E_NPCAL_SKIPATK
      --+|[i][*R:attack_type:] Weapon Attack:[/i] [*R:attack_tohitrange]
      --+|[i]Hit:[/i] [*R:attack_onhit]
    --:_Lib5E_NPCAL_SKIPATK|
    --+|[*R:description]
    
    --Rnext| 

  --^_Lib5E_NPCAL_Loop| 
  --:_Lib5E_NPCAL_Done| 

--<|

--:Lib5E_NPC_Legendary_Action_List|character_id; header
  --&_Lib5E_Temp| 
  --&_Lib5E_FoundOne|0 
  --Rfirst|[%1%];repeating_npcaction-l 
  --?"[*R:name]" -eq "NoRepeatingAttributeLoaded"|_Lib5E_NPCLAL_Done

  -->SECTION_HEADER|[%2%] ([*[%1%]:npc_legendary_actions])

  --+|[*[%1%]:npc_legendary_actions_desc]
  
  --:_Lib5E_NPCLAL_Loop|
    --/rDump|  
    --?"[*R:name]" -eq "NoRepeatingAttributeLoaded"|_Lib5E_NPCLAL_Done
    --&_Lib5E_NameTemp|[*R:name] 
    --~_Lib5E_NameTemp|string;replace;(; ;[&_Lib5E_NameTemp] --~_Lib5E_NameTemp|string;replace;); ;[&_Lib5E_NameTemp]
    --+|[br][b][u][&_Lib5E_NameTemp][/u][/b][r][button]&#x1F409;::~[%1%]|[*R>npc_action][/button][/r][br]
    --+|[*R:description]

    --Rnext| 
  --^_Lib5E_NPCLAL_Loop| 
  --:_Lib5E_NPCLAL_Done| 

--<|

--:Lib5E_NPC_Attribute_Summary|character_id
--+|[t width=100%][tr][td][b]Str[/b] [*[%1%]:strength] ([*[%1%]:strength_mod])[/td][td][b]Dex[/b] [*[%1%]:dexterity] ([*[%1%]:dexterity_mod])[/td][td][b]Con[/b] [*[%1%]:constitution] ([*[%1%]:constitution_mod]) [/td][/tr]
       [tr][td][b]Int[/b] [*[%1%]:intelligence] ([*[%1%]:intelligence_mod])[/td][td][b]Wis[/b] [*[%1%]:wisdom] ([*[%1%]:wisdom_mod])[/td][td][b]Cha[/b] [*[%1%]:charisma] ([*[%1%]:charisma_mod])[/td][/tr]
       [tr][td][b]AC[/b] [*[%1%]:npc_ac][/td][td][b]HP[/b][*[%1%]:hp]/[*[%1%]:hp^][/td][td][b]CR[/b] [*[%1%]:npc_challenge][/td][/tr][/t] --<|

--:Lib5E_NPC_Qualities_Summary|character_id
--+Type|[*[%1%]:npc_type] --?"[*[%1%]:npc_vulnerabilities]" -eq ""|_Lib5E_NPCQS_Skip1 --+Vulnerable|[*[%1%]:npc_vulnerabilities]
--:_Lib5E_NPCQS_Skip1| --?"[*[%1%]:npc_resistances]" -eq ""|_Lib5E_NPCQS_Skip2 --+Resistant|[*[%1%]:npc_resistances] --:_Lib5E_NPCQS_Skip2| --?"[*[%1%]:npc_immunities]" -eq ""|_Lib5E_NPCQS_Skip3 --+Immune|[*[%1%]:npc_immunities]
--:_Lib5E_NPCQS_Skip3| --?"[*[%1%]:npc_condition_immunities]" -eq ""|_Lib5E_NPCQS_Skip4 --+Cond. Immune|[*[%1%]:npc_condition_immunities] --:_Lib5E_NPCQS_Skip4| --?"[*[%1%]:npc_senses]" -eq ""|_Lib5E_NPCQS_Skip5 --+Cond. Immune|[*[%1%]:npc_senses]
--:_Lib5E_NPCQS_Skip6| --<|

--/|==================  Global NPC Settings =======================
--:GLOBAL_NPC_SETTINGS|
  --#hidecard|0

  -->SECTION_HEADER|Whisper Rolls
  --+|[c][rbutton]Never::APPLY_NPC_GLOBAL_SETTING;WR_Never[/rbutton]
         [rbutton]Toggle::APPLY_NPC_GLOBAL_SETTING;WR_Toggle[/rbutton]
         [rbutton]Query::APPLY_NPC_GLOBAL_SETTING;WR_Query[/rbutton]
         [rbutton]Always::APPLY_NPC_GLOBAL_SETTING;WR_Always[/rbutton]
     [/c]

  -->SECTION_HEADER|Roll Queries (Advantage)
  --+|[c][rbutton]Never::APPLY_NPC_GLOBAL_SETTING;RA_Never[/rbutton]
         [rbutton]Toggle::APPLY_NPC_GLOBAL_SETTING;RA_Toggle[/rbutton]
         [rbutton]Query::APPLY_NPC_GLOBAL_SETTING;RA_Query[/rbutton]
         [rbutton]Always::APPLY_NPC_GLOBAL_SETTING;RA_Always[/rbutton]
     [/c]

  -->SECTION_HEADER|Automatic Damage Rolls
  --+|[c][rbutton]On::APPLY_NPC_GLOBAL_SETTING;DT_On[/rbutton]
         [rbutton]Off::APPLY_NPC_GLOBAL_SETTING;DT_Off[/rbutton]
     [/c]

  -->SECTION_HEADER|NPC Name in Rolls
  --+|[c][rbutton]Show::APPLY_NPC_GLOBAL_SETTING;NPCName_Show[/rbutton]
         [rbutton]Hide::APPLY_NPC_GLOBAL_SETTING;NPCName_Hide[/rbutton]
         
     [/c]

  -->SECTION_HEADER|Dex Tiebreaker
  --+|[c][rbutton]On::APPLY_NPC_GLOBAL_SETTING;DTB_On[/rbutton]
         [rbutton]Off::APPLY_NPC_GLOBAL_SETTING;DTB_Off[/rbutton]
     [/c]

  --/>SECTION_HEADER|Configure ChatSetAttr API Script
  --/+|[c][rbutton]On::APPLY_NPC_GLOBAL_SETTING;Config[/rbutton]

  -->FOOTER_BUTTONS_MAIN|
  --X|

--<|

--:APPLY_NPC_GLOBAL_SETTING|MODE
  --&Mode|[&reentryval]
  --#Title|Apply Global Setting
  --+|[c][b]Hang on, this may take a minute...[/b][/c]

  --C[&Mode]|WR_Never:WR_NEVER|WR_Always:WR_ALWAYS|WR_Toggle:WR_TOGGLE|WR_Query:WR_QUERY|RA_Never:RA_NEVER|RA_Toggle:RA_TOGGLE|RA_Query:RA_QUERY|RA_Always:RA_ALWAYS|NPCName_Show:NPCNAME_SHOW|NPCName_Hide:NPCNAME_HIDE|DTB_On:DTB_ON|DTB_Off:DTB_OFF|DT_On:DT_ON|DT_Off:DT_OFF|Config:SETATTR_CONFIG

    --/| *|Invalid Option ([&Mode]) in APPLY_NPC_GLOBAL_SETTING
    --X|

  --:WR_NEVER|
    --#leftsub|Never Whisper Rolls
    --@setattr|_allgm _replace _wtype|
    --X|
  --:WR_ALWAYS|
    --#leftsub|Always Whisper Rolls
    --@setattr|_allgm _replace _wtype|'/w gm '
    --X|
  --:WR_TOGGLE|
    --#leftsub|Toggle Whisper Rolls  
    --@setattr|_allgm _replace _wtype|\at{whispertoggle}
    --X|
  --:WR_QUERY|
    --#leftsub|Query Whisper Rolls  
    --@setattr|_allgm _replace _wtype|\ques{Whisper\ques\|Public Roll,\|Whisper Roll,/w gm }
    --X|

  --:RA_NEVER|
    --#leftsub|Never Roll Advantage
    --&P1|{{normal=1}
    --&P1|+} {{r2=[[0d20
    --@setattr|_allgm _replace _rtype|[&P1]
    --X|

  --:RA_TOGGLE|
    --#leftsub|Toggle Roll Advantage  
    --@setattr|_allgm _replace _rtype|\at{advantagetoggle}
    --X|

  --:RA_QUERY|
    --#leftsub|Query Roll Advantage  
    --@setattr|_allgm _replace _rtype|\at{queryadvantage}
    --X|

  --:RA_ALWAYS|
    --#leftsub|Always Roll Advantage
    --&P1|{{always=1}
    --&P1|+} {{r2=[[\at{d20}
    --@setattr|_allgm _replace _rtype|[&P1]

    --X|
  --:NPCNAME_SHOW|
    --#leftsub|Show NPC Name on Roll
    --&P1|{{charname=\at{npc_name}
    --&P1|+} --&P1|+}

    --&P2|{{name=\at{npc_name}
    --&P2|+} --&P2|+}

    --@setattr|_allgm _replace _charname_output|[&P1] _npc_name_flag|[&P2]

    --X|
  --:NPCNAME_HIDE|
    --#leftsub|Hide NPC Name on Roll
    --@setattr|_allgm _replace _npc_name_flag|0 _charname_output|0
    --X|
  --:DTB_ON|
    --#leftsub|Dex Tiebreaker On
    --@setattr|_allgm _replace _init_tiebreaker|\at{dexterity}/100
    --X|
  --:DTB_OFF|
    --#leftsub|Dex Tiebreaker Off
    --@setattr|_allgm _replace _init_tiebreaker|0
    --X|

  --:DT_ON|
    --#leftsub|Roll Damage Automaticaly
    --@setattr|_allgm _replace _dtype|full
    --X|  

  --:DT_OFF|
    --#leftsub|Do Not Roll Damage Automaticaly
    --@setattr|_allgm _replace _dtype|pick
    --X|  

  --:SETATTR_CONFIG|
    --#leftsub|Configure ChatSetAttr
    --@setattr-config|
  --X|
--<|

--:TO_GETFIRSTITEM|
  --/| Sets:gTO_ITEM_NAME
  --/|      gTO_ITEM_TOKENID 
  --/|      gTO_ITEM_POS
  --/|      gTO_ITEM_NDX(Roll Var: 1 for first time; 0 if empty or end)
  --/|      gTO_ITEM_FORMULA
  --/|      gTO_ITEM_PAGEID
  --/|      gTO_ITEM_TYPE(Character;NPC;Graphic;Custom);

  --/| Load Turnorder (JSON) list into string variable (&TO)         
  --/| *|TO_GETFIRSTITEM Called
  --/| Initialize just in case we've called this before.
  --=gTO_ITEM_NDX|0    
  --&gTO_ITEM_TYPE|
  --&gTO_ITEM_NAME|
  --&gTO_ITEM_TOKENID|
  --&gTO_ITEM_POS|
  --&gTO_ITEM_FORMULA|
  --&gTO_ITEM_PAGEID|

  --&TO|[*C:turnorder]

  --/| Parse string to prep for loading into an array         
  --~TO|string;replaceall;{;;[&TO]
  --~TO|string;replaceall;},;\\\;[&TO]
  --~TO|string;replaceall;};;[&TO]
  --~TO|string;replaceall;,";^;[&TO]
  --~TO|string;replaceall;id":";;[&TO]
  --~TO|string;replaceall;pr":;;[&TO]
  --~TO|string;replaceall;custom":;;[&TO]
  --~TO|string;replaceall;formula":;;[&TO]
  --~TO|string;replaceall;";;[&TO]
  --~TO|string;replaceall;[;;[&TO]
  --~TO|string;replaceall;];;[&TO]

  --~|array;fromstring;aryTO;\\\;[&TO]  

  --/| Now that it's loaded into array, get first record and split into it's fields
  --~aryRec|array;getfirst;aryTO
  --/| *Rec|[&aryRec]
  --?[&aryRec] -eq ArrayError|TO_DONE

  --/| Load the record fields into global memory variables 
  -->TO_LOAD_DATA|

--<|

--:TO_GETNEXTITEM| Assumes that TO_GETFIRSTITEM was previously called
--/| sets gTO_ITEM_NDX to 0 when end of list is reached
  --/| *|TO_GETNEXTTITEM Called
  --~aryRec|array;getnext;aryTO
  --?[&aryRec] -eq ArrayError|TON_EOD
  --&Item1| --&Item2| --&Item3| --&Item4| --&Item5| 
  --~Item|string;split;^;[&aryRec]
  -->TO_LOAD_DATA|
  --^TON_EXIT|
  --:TON_EOD|
    --=gTO_ITEM_NDX|0
    --&gTO_ITEM_TYPE| --&gTO_ITEM_NAME| --&gTO_ITEM_TOKENID| --&gTO_ITEM_POS| --&gTO_ITEM_FORMULA| --&gTO_ITEM_PAGEID|
  --:TON_EXIT|
--<| Return 

--:TO_LOAD_DATA| Load data from array record into global variables based on context
  --/| *|TO_LOAD_DATA Called
  --=gTO_ITEM_NDX|[$gTO_ITEM_NDX] + 1
  --&Item1| --&Item2| --&Item3| --&Item4| --&Item5| 
  --~Item|string;split;^;[&aryRec]
  --/| *|Items([$ItemCount]) 1:[&Item1] 2:[&Item2] 3:[&Item3] 4:[&Item4] 5:[&Item5]

  --?"[&Item1]" -eq "-1"|TOLD_ITEM_CUSTOM
  --/| *Debug|Represents: "[*[&Item1]:t-represents]"

  --&TokenId|[&Item1]
  --/| *TokenId/Rep|[&TokenId] / [*[&TokenId]:t-represents]
  --?"[*[&TokenId]:t-represents]" -ninc "-"|TOLD_IS_GRAPHIC

  --&CharId|[*[&TokenId]:t-represents]
  --?"[*[&TokenId]:NPC]" -ne "1"|TOLD_IS_CHARACTER
  
  --/| IS NPC
    --/| *NPC|[*[&Item1]:t-name] ([&Item2])
    --&gTO_ITEM_TYPE|NPC
    --&gTO_ITEM_NAME|[*[&Item1]:t-name]
    --&gTO_ITEM_TOKENID|[&Item1]
    --&gTO_ITEM_POS|[&Item2]
    --&gTO_ITEM_FORMULA|
    --&gTO_ITEM_PAGEID|[&Item4]
    --^TOLD_DONE|

  --:TOLD_IS_GRAPHIC|
    --&gTO_ITEM_TYPE|Graphic
    --&gTO_ITEM_NAME|[*[&Item1]:t-name]
    --&gTO_ITEM_TOKENID|[&Item1]
    --&gTO_ITEM_POS|[&Item2]
    --&gTO_ITEM_FORMULA|
    --&gTO_ITEM_PAGEID|
    --^TOLD_DONE|

  --:TOLD_IS_CHARACTER|
    --&gTO_ITEM_TYPE|Character
    --&gTO_ITEM_NAME|[*[&Item1]:t-name]
    --&gTO_ITEM_TOKENID|[&Item1]
    --&gTO_ITEM_POS|[&Item2]
    --&gTO_ITEM_FORMULA|
    --&gTO_ITEM_PAGEID|
    --^TOLD_DONE|

  --:TOLD_ITEM_CUSTOM|
    --&gTO_ITEM_TYPE|Custom
    --&gTO_ITEM_NAME|[&Item3]
    --&gTO_ITEM_TOKENID|
    --&gTO_ITEM_POS|[&Item2]
    --&gTO_ITEM_FORMULA|[&Item4] 
    --&gTO_ITEM_PAGEID|
    --^TOLD_DONE|
  --:TOLD_DONE|
--<|Return
}}
