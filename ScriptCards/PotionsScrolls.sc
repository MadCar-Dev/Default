!script {{  
  
  --/|Script Name : Potions and Scrolls
  --/|Version     : 0.1
  --/|Requires SC : 1.4.0+, Chatsetattr
  --/|Author      : Will M.

  --/|Description : A consumables management tool. Supports the following:
  --/|            :  * Reports Potions, Scrolls, Oils, Dust in players inventory grouped by character
  --/|            :  * Adding a new item to players inventory
  --/|            :  * Increasing and decreasing item counts 
  --/|            :  * Transfering/Moving items between player inventory
  --/|            :  * A Player can Only Add/Inc/Dec/Move from CharacterSheets they control
  --/|            :  * A player can Move an item from a charactersheet they control to one they don't
  --/|            :  * Display compendium info for well-known Potions and such 


  --#title|Potions & Scrolls
  --#reentrant|Potions
  --:TOP|

  --&FColor1|#000000
  --&FColor2|#000000
  --&BColor1|#FFFFFF
  --&BColor2|#e6e3e3
  
  --#titleCardBackground|[&SendingPlayerColor]
  --#titlefontsize|1.0em
  --#titlefontlineheight|1.2em
  --#titlefontface|Courier
  --#norollhighlight| 0
  --#hideTitleCard| 0
  --#bodyfontsize| 10px
  --#bodyfontface|Courier
  --#oddrowbackground|[&BColor1]
  --#oddrowfontcolor|[&FColor2]
  --#evenrowbackground|[&BColor1]
  --#evenrowfontcolor|[&FColor2]
  --#buttonbackground|[&BColor1]
  --#buttonbordercolor|[&BColor1]
  --#buttontextcolor|#0905f2

  --#whisper|self, gm
  --#debug|1

  --#rightsub|IsGM: [&SendingPlayerIsGM]
  --/+ID|[&SendingPlayerID]
  --#leftsub|[&SendingPlayerName]
  --/+As|[&SendingPlayerSpeakingAs]
  --/+Color|[&SendingPlayerColor][hr]

  --&tStyle|style="width:100%;text-align:left;padding:0px;border-spacing:0px;border-collapse:collapse;text-shadow: 0px 0px 0px [&BColor1];border: 0px dashed [&BColor1];"
  --&trStyle1|style="border:0px dashed [&FColor2];"
  --&trStyle2|style="border:0px dashed [&BColor1];"
  --&tdStyle1|style="width:5%;text-align:center;background-color:[&BColor1];font-size:110%;font-weight:bold;"
  --&tdStyle2|style="width:65%;text-align:left;background-color:[&BColor1];font-size:100%;font-weight:bold;"
  --&tdStyle3|style="width:30%;text-align:right;background-color:[&BColor1];font-size:80%;font-weight:bold;"
  
  --&t|
  --=PCntr|0

  --~tokencnt|array;pagetokens;alltokens;@{selected|token_id};pc
  --~TokenId|array;getfirst;alltokens

  --:CHAR_LOOP_START| Loop through all the items in the list
  --?[&TokenId] -eq ArrayError|DONE

		--/|Filter out MapNotes
		--?"[*[&TokenId]:t-bar1_value]" -eq "MapNote" |NEXT_CHAR

	  --&CharId|[*[&TokenId]:t-represents]

		--&EditRights|0
		--?[&SendingPlayerIsGM] -eq 1|&EditRights;1
		--?"[*[&CharId]:controlledby]" -inc [&SendingPlayerID]|&EditRights;1
		--?"[*[&CharId]:controlledby]" -inc all|&EditRights;1

	  --Rfirst|[&CharId];repeating_inventory
	  --:INV_LOOP_TOP|

	    --?"[*R:name]" -eq NoRepeatingAttributeLoaded|INV_LOOP_END
	    --~ITEM|string;touppercase;[*R:itemname]

	    --/| List of items to show in List of consumable items
	    --?"[&ITEM]" -inc "POTION"|ADD_ITEM
	    --?"[&ITEM]" -inc "OIL"|ADD_ITEM
	    --?"[&ITEM]" -inc "DUST"|ADD_ITEM
	    --?"[&ITEM]" -inc "SCROLL"|ADD_ITEM	    	    

	    --^SKIP_ITEM|
	    --:ADD_ITEM|

	    	--&IC|[*R:itemcount]
	    	--?X[&IC] -eq X|&IC;1

	    	-->ROLL20_ITEM_LINK|[*R:itemname]
				--=PCntr|[$PCntr] + 1	          	

	      --&t|+ [tr [&trStyle1]]
		           	[td [&tdStyle1]][&IC][/td]
		            [td [&tdStyle2]][button][*R:itemname]::[&zROLL20_MagicItem_Link][/button][/td]


		    --?[&EditRights] -eq 1|ADD_COMMANDS
			    --&t|+ [td [&tdStyle3]][/td][/tr]
			    --^SKIP_ITEM|
		    --:ADD_COMMANDS|
			    --&t|+ [td [&tdStyle3]][rbutton]+::INC_ITEMCOUNT;[&CharId]\[*R>itemcount][/rbutton]
			           								  [rbutton]-::DEC_ITEMCOUNT;[&CharId]\[*R>itemcount][/rbutton]
			           								  [rbutton]->::MOVE_ITEM;[&CharId]\[*R>itemcount][/rbutton][/td][/tr]

	    --:SKIP_ITEM|

	    --Rnext|
	  --^INV_LOOP_TOP|Back to top of loop
	  --:INV_LOOP_END|

  	--&CN|[*[&CharId]:character_name]
  	--&ImgLink|[*[&TokenId]:t-imgsrc]
		--&AddBtn|
    --?[&EditRights] -ne 1|SKIP_NEWITEM_BUTTON
	  	--&AddBtn|[rbutton]Add Potion/Scroll::ADD_NEW_ITEM;[&CharId][/rbutton]

		--:SKIP_NEWITEM_BUTTON|

	  --&hdrstyle_T|style="width:100%;padding:0px;border-spacing:0px;border-collapse:collapse;border:1px solid [&FColor1]"
	  --&hdrstyle_TR|style="border:0px solid #FFFFFF"
	  --&hdrstyle_TD1|style="width:70%;background-color:[&BColor2]; color:[&FColor1]; font-size:110%;font-weight:bold;text-align:left"
	  --&hdrstyle_TD2|style="width:30%;background-color:[&BColor2]; color:[&FColor1]; font-size:110%;font-weight:bold;text-align:right"	  
	 	--#buttonbackground|[&BColor2]
	  --#buttonbordercolor|[&BColor2]
	  --+|[t [&hdrstyle_T]][tr [&hdrstyle_TR]][td [&hdrstyle_TD1]][img width=30][&ImgLink][/img] [&CN][/td]
	  																				[td [&hdrstyle_TD2]] [&AddBtn][/td][/tr][/t]
	 	--#buttonbackground|[&BColor1]
  	--#buttonbordercolor|[&BColor1]	 	

  	--/|+Controlled By|[*[&CharId]:controlledby]

	  --?[$PCntr.Raw] -eq 0|[
	  	--+|[B][I][c]No potions or scrolls found in inventory[/c][/I][/B]
	  --]|[
		  --&t|[t [&tStyle]] [&t] [/t]
		  --+|[&t]
		--]|

	--:NEXT_CHAR|

	--~TokenId|array;getnext;alltokens

	--&t|
	--=PCntr|0
	--^CHAR_LOOP_START|
--:DONE|

  -->FOOTER_BUTTONS_MAIN|
  --X|

--:ADD_NEW_ITEM|
	--#Title|Add New Item
  --~Arg|string;split;\;[&reentryval]
  --&CharId|[&Arg1]

  	--&CName|[*[&CharId]:character_name]
  	--I;Add new Potion or Scroll for [&CName]|q;ItemName;New Potion/Scroll?

  	--@setattr|_charid [&CharId] _silent _repeating_inventory_-CREATE_itemname|[&ItemName] _repeating_inventory_-CREATE_itemcount|1 _repeating_inventory_-CREATE_equipped|1 _repeating_inventory_-CREATE_useasresource|1 

		--&LogMsg|[*[&CharId]:character_name] added a [&ItemName] to their inventory
		-->LOG|"[&LogMsg]"
		--+|[&LogMsg]
		-->FOOTER_BUTTONS_MAIN|
	--x|
--<

--:INC_ITEMCOUNT|
	--#Title|Increase Item Count
  --~Arg|string;split;\;[&reentryval]
  --&CharId|[&Arg1]
  --&ItemCountField|[&Arg2]
  --@setattr|_charid [&CharId] _silent _mod _[&ItemCountField]|1

	--~RFName|string;before;_itemcount;[&ItemCountField]
	--&ItemNameField|[&RFName]_itemname
	--&ItemName|[*[&CharId]:[&ItemNameField]]
	--=ItemCount|[*[&CharId]:[&ItemCountField]] + 1
  --&LogMsg|[*[&CharId]:character_name] increased inventory of [&ItemName] to [$ItemCount.Raw]
	-->LOG|"[&LogMsg]"
	--+|[&LogMsg]
	-->FOOTER_BUTTONS_MAIN|
  
  --/|^TOP|
  --x|
--<|

--:DEC_ITEMCOUNT|
	--#Title|Reduce Item Count
  --~Arg|string;split;\;[&reentryval]
  --&CharId|[&Arg1]
  --&ItemCountField|[&Arg2]
  --=ItemCount|[*[&CharId]:[&ItemCountField]]

  --?[$ItemCount.Raw] -eq 1|DELETE_ITEM
  	--@setattr|_charid [&CharId]  _silent _mod _[&ItemCountField]|-1
		
		--~RFName|string;before;_itemcount;[&ItemCountField]
		--&ItemNameField|[&RFName]_itemname
		--&ItemName|[*[&CharId]:[&ItemNameField]]
		--=ItemCount|[$ItemCount.Total] - 1
		--&LogMsg|[*[&CharId]:character_name] decreased inventory of [&ItemName] to [$ItemCount.Raw]
		-->LOG|"[&LogMsg]"
		--+|[&LogMsg]
		-->FOOTER_BUTTONS_MAIN|

  	--x|

  --:DELETE_ITEM|

  	--~RFName|string;before;_itemcount;[&ItemCountField]
  	--/|*RFName|[&RFName]
		--&ItemNameField|[&RFName]_itemname
		--&ItemName|[*[&CharId]:[&ItemNameField]]

  	--@delattr|_charid [&CharId]  _silent _[&RFName]

		--&LogMsg|[*[&CharId]:character_name] used the last of [&ItemName]
		-->LOG|"[&LogMsg]"
		--+|[&LogMsg]
		-->FOOTER_BUTTONS_MAIN|

  --/|^TOP|
  --x|
--<|

--:MOVE_ITEM|[From_Char], [FC_Item], [FC_ItemName]
	--#Title|Move Item to Other Player

  --~Arg|string;split;\;[&reentryval]
  --&From_CharId|[&Arg1]
  --&ItemNameField|[&Arg2]

	--~RFName|string;before;_itemcount;[&ItemNameField]
	--&ItemNameField|[&RFName]_itemname
	--&ItemName|[*[&From_CharId]:[&ItemNameField]]

  --I;Give "[&ItemName]" to a friend?|t;To_TokenId;Give [&ItemName] to?
  --/| Verify this is a valid Party member

  --?"[*[&To_TokenId]:playercharacter]" -eq 1|MI_PC
  --?"[*[&To_TokenId]:t-represents]" -ninc "-"|MI_SKIP
  --?"[*[&To_TokenId]:npc]" -eq 1|MI_SKIP
  --?"[&To_TokenId]" -eq "[&From_CharId]"|MI_SKIP

  --:MI_PC|
  --&To_CharId|[*[&To_TokenId]:t-represents]

  --/|Add Item to the To Char Inventory

  	--/| Check to see if it already exists, and just add it to their inventory if it doesn't
	  --Rfirst|[&To_CharId];repeating_inventory
	  --~New_Item|string;touppercase;[&ItemName]
	  --~New_Item|string;trim;[&New_Item]
	  --:MI_INV_LOOP_TOP|

	    --?"[*R:name]" -eq NoRepeatingAttributeLoaded|MI_INV_LOOP_NOT_FOUND
	    --~To_Item|string;touppercase;[*R:itemname]
	  	--~To_Item|string;trim;[&To_Item]

	    --/|*Compare|[&To_Item] -ne [&New_Item]
	    --?"[&To_Item]" -ne "[&New_Item]"|MI_NEXT_ITEM
	    	  --@setattr|_charid [&To_CharId] _silent _mod _[*R>itemcount]|1
	    	  --^MI_INV_LOOP_END|
	    --:MI_NEXT_ITEM|
		    --Rnext|
		    --^MI_INV_LOOP_TOP|

  	--:MI_INV_LOOP_NOT_FOUND|
  		--/| Didn't Find if we got here, so add it as a whole new item
  		--@setattr|_charid [&To_CharId] _silent _repeating_inventory_-CREATE_itemname|[&ItemName] _repeating_inventory_-CREATE_itemcount|1 _repeating_inventory_-CREATE_equipped|1 _repeating_inventory_-CREATE_useasresource|1 
  	
  	--:MI_INV_LOOP_END|

  --/|Remove Item from the From char inventory
	--&ItemCountField|[&RFName]_itemcount
  --=ItemCount|[*[&From_CharId]:[&ItemCountField]]
  --*From ItemCount| [$ItemCount.Raw]
  --?[$ItemCount.Raw] -eq 1|MI_DELETE_ITEM
  	--@setattr|_charid [&From_CharId]  _silent _mod _[&ItemCountField]|-1
  	--^MI_DONE|

  --:MI_DELETE_ITEM|
  	--@delattr|_charid [&From_CharId] _silent _[&RFName]
  	--^MI_DONE|

  --:MI_DONE|

	--&LogMsg|[*[&From_CharId]:character_name] handed [*[&To_CharId]:character_name] a [&ItemName]
	-->LOG|"[&LogMsg]"
	--+|[&LogMsg]
	-->FOOTER_BUTTONS_MAIN|
	--x|

	--:MI_SKIP|
		--+ERROR|Not a valid Player Character
		-->FOOTER_BUTTONS_MAIN|		
	--X|

--<|

--:LOAD_CHARACTERIDS|
  --/|Campaign SheetId
  --&CSId|-MY16YzwbcQ-rc18_HjI
  --&gCharIds|[*[&CSId]:characters]
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

--:SECTION_HEADER|Title
  --&hdrstyle_T|style="width:100%;padding:1px;border-spacing:0px;border-collapse:collapse;border:1px solid #FFFFFF;"
  --&hdrstyle_TR|style="border:0px solid #000000;"
  --&hdrstyle_TD|style="width:100%;background-color:[&FColor2]; color:#FFFFFF; font-size:110%;font-weight:bold;text-align:left"
  --+|[t [&hdrstyle_T]][tr [&hdrstyle_TR]][td [&hdrstyle_TD][%1%][%2%][/td][/tr][/t]
--<|

--:FOOTER_BUTTONS_MAIN|

	--?[&SendingPlayerIsGM] -eq 1|[
    --+|[l][rbutton]Refresh::TOP[/rbutton][/l]
            [r][button]MapNotes::~Mule|MapNote-Tools[/button]
            [button]NPC::~Mule|NPC-Tools[/button]
            [button]DM::~Mule|DM-Tools[/button][/r]

  --]|[
    --+|[r][rbutton]Refresh::TOP[/rbutton][/r]
 	--]

--<|

--:LOG|Text to log
    --~DT|system;date;getdatetime
    --@note-log|[&DT]: [%1%]
--<|

--:DNDBEYOND_MAGICITEM_LINK|Magic Item 
  --/|Format is https://www.dndbeyond.com/magic-items/potion-of-healing
  --~MN|string;replaceall; ;-;[%1%]
  --&zDnDBeyond_MagicItem_Link|https://www.dndbeyond.com/magic-items/[&MN]
--<|

--:ROLL20_ITEM_LINK|ITEM NAME
  --/|Format is https://app.roll20.net/compendium/dnd5e/item name
  --/| ~MN|string;replaceall; ;-;[%1%]
  --&zROLL20_MagicItem_Link|https://app.roll20.net/compendium/dnd5e/[%1%]
--<|

}}
