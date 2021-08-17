!scriptcard {{ 
  --/|Script Name : Map Note Tools
  --/|Version     : 4.0
  --/|Requires SC : 1.4.0+, TokenMod, Chatsetattr, gmnotes, SelectManager, SpawnDefaultToken
  --/|Author      : Will M.

  --/|Description : Allows DM to add Site Note Tokens for location based notes
  --/|
  --/|Updates: 7/30/2021: Now works with latest One-Click version of SpawnDefaultToken
  --/|Known Issues: When updating the list of notes due to a change in the name or type, it may not represent the new name/type in the list.
  --/|              This is due to a timing issue between calls to TokenMod and Reading the token data.  Just refresh again to see the change. 

  --/|Future Update: Convert Token to MapNote.  Many times there already exist a numbered token on maps.  Just need to call tokenmod to configure it appropriately.

  --/|Setup: 
  --/| 1 - A new Character Sheet named MapNote
  --/| 2 - Create a rollable token table with 9 different tokens images
  --/|  (1-Site, 2-info, 3-Encounter, 4-Trap, 5-Secret, 6-Shop, 7-Clue, 8-Quest, 9-Other)
  --/| 3 - Assign this multisided Rollable Token to the MapNote character sheet

  --/| Characteristics of a MapNote:
  --/|  TokenName: Note Name (Info, Barbershop, Baker, Event, Encounter, ...)
  --/|  Bar1_Value: MapNote 
  --/|  Bar2_Value: Note Type (Location, Encounter, General, ...)
  --/|  Bar3_Value: Quick Note Text (Info, Barbershop, Baker, Event, Encounter, ...)
  --/|  Layer: Normally exist on the GM layer, but could change to object layer if identifies a place the players discover
  --/|  Initial Color set based on Type:  Red-Encounter, Blue-Information, Green-Location
  --/|  Aura: Used to higlight a note

  --&reentryval|
  --:TOP|
  --#reentrant|MapNote Tools
  --#title|Map Notes

  --&FColor1|#FFCC00 
  --&FColor2|#FFB000
  --&BColor1|#282828
  --&BColor2|#3A3B3C
  --#titlecardbackgroundimage| linear-gradient( to bottom, #FFB000, #282828 )  
  --#titlefontsize|2.0em
  --#titlefontlineheight|1.2em
  --#titlefontface|Courier
  --#norollhighlight| 0
  --#hideTitleCard| 0
  --#bodyfontsize| 10px
  --#bodyfontface|Courier
  --#oddrowbackground|[&BColor1]
  --#oddrowfontcolor|[&FColor2]
  --#evenrowbackground|[&BColor1]
  --#evenrowfontcolor|[&FColor1]
  --#buttonbackground|[&BColor1]
  --#buttontextcolor|[&FColor1]
  --#buttonbordercolor|[&BColor1]
  --#buttonfontsize|x-small
  --#buttonfontface|Courier
  --#usehollowdice|0
  --#emotestate|off
  --#whisper|gm
  --#debug|0
  --#timezone|America/Chicago
  --#executionlimit|500000

  --#sourceToken|@{selected|token_id}
  --#activepage|[*S:t-_pageid]

  --Ssettings|MapNoteTools

  -->SECTION_HEADER|Macros  
  --+|[c][button]&#x1F4CD;::~Mule|Page-Assets[/button]
         [button]&#x1F4CB;::~Mule|Menu-Char-Token-Info[/button]
         [button]&#x1F527;::~Mule|Menu-Utility-Macros[/button]
         [button]&#x1F6A6;::~Mule|Menu-TurnOrder-Macros[/button]
         [button]&#x1F3AD;::~Mule|Player-Macros[/button]
         [rbutton]&#x2699;::GLOBAL_NPC_SETTINGS[/rbutton]
         [button]&#x1F310;::~Mule|DM-Links[/button]         
         [button]&#x1F39A;::~Mule|NPC-Layers[/button]
     [/c]

  -->SECTION_HEADER|Map Notes
  --&tStyle|style="width:100%;text-align:left;padding:1px;border-spacing:0px;border-collapse:collapse;text-shadow: 0px 0px 0px [&BColor1];border: 0px dashed [&BColor1];"
  --&trStyle1|style="border:1px dashed [&FColor2];"
  --&trStyle2|style="border:0px dashed [&BColor1];"
  --&tdStyle1|style="width:60%;text-align:left;background-color:#282828;font-size:100%"
  --&tdStyle2|style="width:40%;text-align:left;background-color:#282828;font-size:100%"
  --&tdStyle3|style="width:100%;background-color:[&BColor1];font-size:100%;font-weight:bold;text-align:center" colspan=2  
  --&t|

  --~tokencnt|array;pagetokens;AllTokens;@{selected|token_id}
  --~TokenId|array;getfirst;AllTokens;char
  --?[&TokenId] -eq ArrayError|ENDLOOP

  --:LOOPCHECK|
  
  --~TName|string;touppercase;[*[&TokenId]:t-bar1_value]
  --/|*t-name|[&TName]
  --?"[&TName]" -ne "MAPNOTE"|SKIP

    --&TNInfo|[br][rbutton][+]::ADD_QUICK_NOTE;[&TokenId][/rbutton][*[&TokenId]:t-bar3_value]
    
    --&t|+ [tr [&trStyle1]][td [&tdStyle1]][rbutton][img width=30 height=30][*[&TokenId]:t-imgsrc][/img] 
           [b][*[&TokenId]:t-name]([*[&TokenId]:t-bar2_value])::FIND_TOKEN;[&TokenId][/rbutton][/b][/td]
           [td [&tdStyle2]]
            [rbutton]Read::GMNOTE;[&TokenId]\Token[/rbutton]
            [rbutton]Name::CHANGE_NOTE_NAME;[&TokenId][/rbutton]
            [rbutton]Type::CHANGE_NOTE_TYPE;[&TokenId][/rbutton]
            [rbutton]Title::TOGGLE_TITLE;[&TokenId][/rbutton]            
            [rbutton]Layer::TOGGLE_LAYER;[&TokenId][/rbutton]
            [rbutton]Aura::TOGGLE_AURA;[&TokenId][/rbutton]
          [/td][/tr]

    --:SKIP|
    --~TokenId|array;getnext;AllTokens
    --?[&TokenId] -ne ArrayError|LOOPCHECK
  --:ENDLOOP|

  --&t|[t [&tStyle]] [&t] [/t]
  --+|[&t]
  
  -->FOOTER_BUTTONS_MAIN|  
  --X|

--:FIND_TOKEN_BY_NAME|Token Name
--/| Parameters: Token Name
--/| Returns/Sets: [&FTOKEN_ID], returns #NA if not available
  --&TN|[%1%]
  --&FTOKEN_ID|#NA

  --~tc|array;pagetokens;PTAry;@{selected|token_id}
  --~tempid|array;getfirst;PTAry

  --:FT_LOOPSTART|
  --?[&tempid] -eq ArrayError|FT_ENDLOOP
    --?"[*[&tempid]:t-name]" -eq "[&TN]"|FT_FOUND
    --~tempid|array;getnext;PTAry
    --?[&tempid] -ne ArrayError|FT_LOOPSTART|FT_EXIT
  --:FT_FOUND|
    --&FTOKEN_ID|[&tempid]
    --/|*FTBN|Found - TokenId: [&FTOKEN_ID]  
  --:FT_EXIT|
  --<|

--/|==================  ReEntry Function ========================
--:SPAWN_MAPNOTE|
  --#title|Create New Map Note
  --#whisper|gm
  --#debug|0
  --#hidecard|0

  --/| Spawn Initial token
  --@forselected+|Spawn _name|MapNote _bar1|MapNote _bar2|Site  _layer|gm _offset|1,0 _side|1 _order|ToFront _isDrawing|1 

  --/|*SMN1|Spawned Note Token

  --&reentryval|NEW
  -->CHANGE_NOTE_NAME|
  --/|*SMN2|TokenId: [&TokenId]  

  --&reentryval|[&TokenId] 
  -->CHANGE_NOTE_TYPE|

--x|

--:READ_NOTE|
--:OPEN_NOTE|
--X|

--:TOGGLE_AURA|
  --#hidecard|1
  --&TokenId|[&reentryval]
  --&Aura|
  --/|*Aura|[*[&TokenId]:t-aura1_radius]
  --?[*[&TokenId]:t-aura1_radius] -gt 0|&Aura;|&Aura;3
  --/|*NewAura|[&Aura]
  --@token-mod|_set aura1_radius|[&Aura] aura1_color|#4a86e8 _ids [&TokenId] _ignore-selected
--X|

--:TOGGLE_TITLE|
  --#hidecard|1
  --&TokenId|[&reentryval]
  --@token-mod|_flip showname _ids [&TokenId] _ignore-selected
--X|

--:CHANGE_NOTE_NAME|
  --&TokenId|[&reentryval]
  --/|&TName|[*[&TokenId]:t-name]

  --/|*CNN1|TokenId: [&TokenId]  

  --I;What is the MapNote Name?|q;NoteName;New MapNote Name?

  --/| For new tokens, there is now TokenId yet established and we need to find it for the token named MapNote
  --&NewToken|0
  --?[&TokenId] -ne NEW|CNN_APPLY

    --&NewToken|1
    -->FIND_TOKEN_BY_NAME|MapNote
    --&TokenId|[&FTOKEN_ID]
    --?[&FTOKEN_ID] -ne #NA|CNN_APPLY
      --+Error|MapNote Not Found 
      --x|

  --:CNN_APPLY|

  --/|*CNN2|TokenId: [&TokenId]    
  --@token-mod|_set name|"[&NoteName]" _ids [&TokenId] _ignore-selected

  --?[&NewToken] -ne 1|TOP
    --<|
--x|

--:CHANGE_NOTE_TYPE|
  --&TokenId|[&reentryval]
  --&TName|[*[&TokenId]:t-name]

  --/|*CNT|TokenId: [&TokenId]  

  --I;Change MapNote Type for: [&TName]|q;NoteType;New MapNote Type?|Site|Info|Encounter|Trap|Secret|Shop|Clue|Quest|Other

  --C[&NoteType]|Site:>Site|Info:>Info|Encounter:>Encounter|Trap:>Trap|Secret:>Secret|Shop:>Shop|Clue:>Clue|Quest:>Quest|Other:>Other
  --=Side|9 --^CHANGE_TYPE|

  --:Site| --=Side|1 --^CHANGE_TYPE|
  --:Info| --=Side|2 --^CHANGE_TYPE|
  --:Encounter| --=Side|3 --^CHANGE_TYPE|
  --:Trap| --=Side|4 --^CHANGE_TYPE|
  --:Secret| --=Side|5 --^CHANGE_TYPE|
  --:Shop| --=Side|6 --^CHANGE_TYPE|
  --:Clue| --=Side|7 --^CHANGE_TYPE|
  --:Quest| --=Side|8 --^CHANGE_TYPE|
  --:Other| --=Side|9 --^CHANGE_TYPE|

  --:CHANGE_TYPE|

  --/|*API|@token-mod|_set bar2_value|"[&NoteType]" currentside|[$Side.Raw] _ids [&TokenId] _ignore-selected
  --@token-mod|_set bar2_value|"[&NoteType]" currentside|[$Side.Raw] _ids [&TokenId] _ignore-selected

  --^TOP|
--x|

--:GMNOTE|TokenID, Mode/Param
  --~Arg|string;split;\;[&reentryval]
  --C[&Arg2]|Token:&Param;token|GMNote:&Param;charnote|Bio:&Param;bio|Avatar:&Param;avatar _notitle|Images:&Param;images _notitle|Config:&Param;config|Help:&Param;help
  --/|*|[&Param] / [&Arg1]
  --@gmnote|_[&Param] _id[&Arg1]

  --X|
--<|

--/|==================  Utility Functions =======================

--:ADD_QUICK_NOTE|TokenId
  --#hidecard|1
  --~Arg|string;split;\;[&reentryval]
  --&TokenId|[&Arg1]
  --I;Add a quick note to [*[&TokenId]:t-name]|q;QN;Add a QuickNote?

  --@token-mod|_set bar3_value|"[&QN]" _ids [&TokenId] _ignore-selected
--X|


--:TOGGLE_LAYER|TokenId
  --#hidecard|1
  --~Arg|string;split;\;[&reentryval]
  --&TokenId|[&Arg1]
  --/|*TOGGLE_LAYER|[*[&TokenId]:t-name] - [*[&TokenId]:t-layer]

  --?"[*[&TokenId]:t-layer]" -eq "objects"|&Layer;gmlayer|&Layer;objects

  -->SET_LAYER|[&TokenId];[&Layer]
--<|

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

--:SECTION_HEADER|Title
  --&hdrstyle_T|style="width:100%;padding:1px;border-spacing:0px;border-collapse:collapse;text-shadow: 0px 0px 0px [&BColor1];border:1px solid [&FColor2];"
  --&hdrstyle_TR|style="border:0px solid [&FColor2];"
  --&hdrstyle_TD|style="width:100%;background-color:[&BColor1]; color:[&FColor1]; font-size:110%;font-weight:bold;text-align:center"
  --+|[t [&hdrstyle_T]][tr [&hdrstyle_TR]][td [&hdrstyle_TD]][c][%1%][/c][/td][/tr][/t]
--<|

--:FOOTER_BUTTONS_MAIN|
    --+|[l][rbutton]+MN::SPAWN_MAPNOTE[/rbutton]
           [rbutton]Cvt::CONVERT_TOKEN_2_MAPNOTE[/rbutton] [/l]
            [r][button]MapNotes::~Mule|MapNote-Tools[/button]
            [button]NPC::~Mule|NPC-Tools[/button]
            [button]DM::~Mule|DM-Tools[/button]
            [/r]
--<|

--:CONVERT_TOKEN_2_MAPNOTE|
  --\|Purpose: Converts an existing Token to the MapNote format:
  --\|  * bar1_value:MapNote
  --\|  * bar2_value:Type (Site, Info, ...)
  --\|  * Name - MapNote Name?
  --\|First - Ask "Currently selected token?" or "Select a diff Token?"
  --&TN|[*S:t-name]
  --&TokenId|[*S:t-id]
  --#Title|Convert Token
  --#buttonbackground|[&BColor2]
  --#buttonbordercolor|[&FColor1]
  
  --+|[c][img width=100][*[&TokenId]:t-imgsrc][/img][/c] 
  --&CharId|[*[&TokenId]:t-represents]
  --+|[c][b][*[&TokenId]:t-name] / [*[&CharId]:character_name][/b][/c]
  --+|[br][hr][br]

  --+|[c][rbutton]Selected Token ([&TN])?::CT2MN_CONVERT;[&TokenId][/rbutton][/c]
  --+|[br]
  --+|[c]- OR -[/c]
  --+|[br]
  --+|[c][rbutton]Other Token::CT2MN_SELECT[/rbutton][/c]
  --+|[br]
  --+|[c]- OR -[/c]
  --+|[br]
  --+|[c][rbutton]Cancel::CT2MN_CANCEL[/rbutton][/c]

  --X|


  --\| *** Select a different token ***
  --:CT2MN_SELECT|
  --I Select the token you wish to convert to a MapNote;Token|t;TokenId;Select token to convert to a MapNote

  --\| Select a different token
  --?"[*[&TokenId]:t-represents]" -inc "-"|[
    --#Title|Select Token
    --&CharId|[*[&TokenId]:t-represents]
    --+|[b]Selected Token ([*[&TokenId]:t-name] / [*[&CharId]:character_name]) is tied to a character sheet, are you sure you want to make this a MapNote?[/b]
    --+|[c][img width=100][*[&TokenId]:t-imgsrc][/img][/c] 
    --+|[c][rbutton]Continue::CT2MN_CONVERT;[&TokenId][/rbutton][/c]
    --+|[br]
    --+|[c]- OR -[/c]
    --+|[br]
    --+|[c][rbutton]Select Diff Token::CT2MN_SELECT[/rbutton][/c]
    --+|[br]
    --+|[c]- OR -[/c]
    --+|[br]
    --+|[c][rbutton]Cancel::CT2MN_CANCEL[/rbutton][/c]
    --X|
  --]|

  --&reentryval|[&TokenId]
  -->CT2MN_CONVERT|

--\| *** Once you have a token to Convert ***
--:CT2MN_CANCEL|
    --^TOP|
    --X|

--\| *** Once you have a token to Convert ***
--:CT2MN_CONVERT|token_id
  --&TokenId|[&reentryval]

  --@token-mod|_set name|"New Note" bar1_value|MapNote bar2_value|New aura1_radius|3 aura1_color|#4a86e8 _on showname  _ids [&TokenId] _ignore-selected

  --+|[c][img width=100][*[&TokenId]:t-imgsrc][/img][/c] 
  --+|[br]
  --+|[b][c]Has been converted to a MapNote[/c][/b]
  --\| Ask what kind? (Site, Info, Secret, ...)
  --\| If it doesn't have a name - ask to give it one. 

  --\| Use tokenmod to adjust

--X|


--:ROLL20_CHARACTERSHEET_LINK|CharId, button caption
  --&zROLL20_CS|https://journal.roll20.net/character/[%1%]
  --&ROLL20_CHARACTERSHEET_BTN|[button][%2%]::[&zROLL20_CS][/button]
--<|

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

}}
