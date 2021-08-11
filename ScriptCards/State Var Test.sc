!scriptcard {{
--/| Test STATE FUNCTIONS	
  --:TOP|
  --#reentrant|StateTest
  --#title|Test State Functions
  --#titleCardBackground|#095c1f
  --#oddRowBackground|#9fbfa7
  --#evenRowBackground|#9fbfa7
  --#buttonbackground|#9fbfa7
  --#buttontextcolor|#000000
  --#buttonbordercolor|#9fbfa7
  --#hidecard|0    
  --#bodyFontSize|11px
  --#whisper|gm
  --#debug|1

  --+|[rbutton]Update/Add Pair of Array Items::DLG_ADD_STATE_PAIR[/rbutton]
  --+|[rbutton]Update State String Variable::DLG_ADD_STATE_STRINGVAR[/rbutton]
  --+|[rbutton]Update State Roll Variable::DLG_ADD_STATE_ROLLVAR[/rbutton]
  --+|[rbutton]Find an Array Item::DLG_FIND_STATE_ITEM[/rbutton]
  --+|[rbutton]Dumps State Vars::DUMP_STATE_VALUES;1[/rbutton]
  --+|[rbutton]Delete an Array Item Pair::DLG_DEL_STATE_PAIR[/rbutton]
  --+|[rbutton]Clear State Memory::ERASE_STATE_VALUES[/rbutton]

--X|

--/|____DIALOG FUNCTIONS FOR TESTING______

--:DLG_ADD_STATE_STRINGVAR|Refernce
	--IAdd State String Variable|q;Val;Value?
	-->ADD_STATE_STRINGVAL|[&Val]
	--^TOP|
--X|
--<|

--:DLG_ADD_STATE_ROLLVAR|Refernce
	--IAdd State Roll Variable|q;Val;Value?
	--=rVal|[&Val]
	-->ADD_STATE_ROLLVAL|[$rVal]
	--^TOP|
--X|
--<|

--:DLG_DEL_STATE_PAIR|Refernce
	--ILocate State Setting to DELETE|q;StRef;Setting Name?
	-->DEL_STATE_VAL|[&StRef]
	--^TOP|
--X|
--<|

--:DLG_ADD_STATE_PAIR|
	--IAdd New State Array Setting|q;StRef;Setting Name?||q;StVal;Value for setting?
	-->PUT_STATE_VAL|[&StRef];[&StVal];
	--^TOP|
--X|
--<|

--:DLG_FIND_STATE_ITEM|
	--ILocate State Setting|q;StRef;Setting Name?
	-->GET_STATE_VAL|[&StRef]
	--+[&StRef]|[&gSTATE_VAL] 
	--+RefNdx/ValNdx|[&gSTATE_REF_NDX] / [&gSTATE_VAL_NDX]
	--^TOP|
--X|
-<

--/|____STATE LIBRARY FUNCTIONS______

--:GET_STATE_VAL|RefName
--/|Assumes data stored in ordered pairs (Ref1;Val1; Ref2;Val2; Ref3;Val3 ... )
--/|Sets/Returns for passed RefName[%1%]
--/|  gSTATE_VAL: Associated value
--/|  gSTATE_VAL_NDX: value of Value Index position
--/|  gSTATE_REF_NDX: value of RefName Index position
--/|  gSTATE_ARY: Array read from State
--/|If Not Found
--/|  gSTATE_VAL, gSTATE_VAL_NDX, gSTATE_VAL_NDX set to "NotFound"

	--/|1. Read State Array into memory
	--~gSTATE_ARY|stateitem;read;array

	--/|2. Attempt to find RefName in array
	--~Ndx|array;indexof;gSTATE_ARY;[%1%]
	--/+FindNdx|[&Ndx]
	--?"[&Ndx]" -eq "ArrayError"|GSV_NOTFOUND

	--/|3. If Found, step to next item and get value, erroring out if not found (something really wrong happened)
	--~|array;setindex;gSTATE_ARY;[&Ndx]
	--~Item|array;getcurrent;gSTATE_ARY
	--?"[&Item]" -eq "ArrayError"|GSV_NOTFOUND
	--/+ItemRef|[&Item]
  --~Item|array;getnext;gSTATE_ARY
	--?"[&Item]" -eq "ArrayError"|GSV_NOTFOUND
	--/+ItemVal|[&Item]

	--/|4. Set global state variables to item and indexes found
	--&gSTATE_VAL|[&Item]
	--&gSTATE_REF_NDX|[&Ndx]
	--~Ndx|array;getindex;gSTATE_ARY
	--&gSTATE_VAL_NDX|[&Ndx]
	--^GSV_EXIT|

	--/|5. If not Found, set global state variables to "NotFound"
	--:GSV_NOTFOUND|
	--&gSTATE_VAL|NotFound
	--&gSTATE_REF_NDX|NotFound
	--&gSTATE_VAL_NDX|NotFound

	--:GSV_EXIT|
--<|

--:PUT_STATE_VAL|RefName, Value
--/|Assumes data stored in ordered pairs (Ref1;Val1; Ref2;Val2; Ref3;Val3 ... )
--/|Adds/Updates Value associated with RefName[%1&] to Value[%2%]
--/|First thing this function does is calls GET_STATE_VAL which loads the following in memory
--/|  gSTATE_VAL: Associated value
--/|  gSTATE_VAL_NDX: value of Value Index position
--/|  gSTATE_REF_NDX: value of RefName Index position
--/|  gSTATE_ARY: State Array
--/|Returns Nothing

	--/|1. Attempt to find the location of the Referenced Item
	-->GET_STATE_VAL|[%1%]
	--?"[&gSTATE_VAL]" -eq "NotFound"|PSV_EXIT

	--/|2. If Found: Remove Current pair, then proceed to add new pair 
	--~|array;removeat;gSTATE_ARY;[&gSTATE_VAL_NDX]
	--~|array;removeat;gSTATE_ARY;[&gSTATE_REF_NDX]
	
	--/|3. If Not Found: Just ddd a new pair of values to the state
  --:PSV_EXIT|
	--~|array;add;gSTATE_ARY;[%1%];[%2%]
	--~gSTATE_ARY|stateitem;write;array
--<|

--:DEL_STATE_VAL|RefName
--/|Assumes data stored in ordered pairs (Ref1;Val1; Ref2;Val2; Ref3;Val3 ... )
--/|Delete Pair (ref/value) for passed RefName[%1%]

	-->GET_STATE_VAL|[%1%]

	--?"[&gSTATE_VAL]" -eq "NotFound"|DSV_NOTFOUND
	--/|2. If Found: Remove Current pair, 
	--~|array;removeat;gSTATE_ARY;[&gSTATE_VAL_NDX]
	--~|array;removeat;gSTATE_ARY;[&gSTATE_REF_NDX]
	--~gSTATE_ARY|stateitem;write;array

	--^DSV_EXIT|
	--:DSV_NOTFOUND|
	--+DELTE_STATE_VAL|Item Not Found ([%1%])
	--^DSV_EXIT|
	--DSV_EXIT:

--<|

--:DUMP_STATE_VALUES| Detail Flag (if 1, also dumps ordered pair of StateArray variables)
--/|Assumes data stored in ordered pairs (Ref1;Val1; Ref2;Val2; Ref3;Val3 ... )
--/|Loads gSTATE_ARY into Memory and dumps its value as a string

	--~gzSTATE|stateitem;read;stringvariable
	--~grSTATE|stateitem;read;rollvariable
	--~gSTATE_ARY|stateitem;read;array
	--~z|array;stringify;gSTATE_ARY
	--+|[c][b]State Values[/b][/c]
	--+String|[&gzSTATE]
	--+Roll|[$grSTATE]
	--+Array|[&z]

	--?"[%1%]"" -eq "1"|DSV_EXIT

	--~ItemRef|array;getfirst;gSTATE_ARY
	--:LOOPSTART|
		--/+Debug|1. Ref: [&ItemRef]
		--?"[&ItemRef]" -eq "ArrayError"|DSV_EXIT
			--~ItemVal|array;getnext;gSTATE_ARY
			--/+Debug|2. Val: [&ItemVal]
			--?"[&ItemVal]" -eq "ArrayError"|DSV_ODD_COUNT
				--+[&ItemRef]|[&ItemVal]
				--~ItemRef|array;getnext;gSTATE_ARY
			--^LOOPSTART|
	--:DSV_ODD_COUNT|
		--+[&ItemRef]|No Value Found for Last Ref Item
	--:DSV_EXIT|
	--X|
--<

--:ERASE_STATE_VALUES| 
--/|Erases all state values

	--&gzSTATE|" "
	--=grSTATE|
	--~|array;define;gSTATE_ARY

	--~gzSTATE|stateitem;write;stringvariable
	--~grSTATE|stateitem;write;rollvariable
	--~gSTATE_ARY|stateitem;write;array
--<|

--:ADD_STATE_STRINGVAL|
	--&zVal|[%1%]
	--~zVal|stateitem;write;stringvariable
--<|
--:ADD_STATE_ROLLVAL|[$rVal]
	--=rVal|[%1%]
	--~rVal|stateitem;write;rollvariable
--<|
}}
