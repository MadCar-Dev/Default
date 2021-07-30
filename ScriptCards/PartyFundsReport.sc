!scriptcard {{ 

  --/|Script Name : Party Funds Report
  --/|Version     : 4.0
  --/|Requires SC : 1.3.7+, 
  --/|Author      : Will M.

  --/|Description : Quick report showing party funds, with conversion to GP-Equiv.

  --:___Formatting___|
  --#title|Party Funds Report
  --#titleCardBackground|#03038a
  --#oddRowBackground|#d8d8e6
  --#evenRowBackground|#FFFFFF
  --#buttonbackground|#FFFFFF
  --#buttontextcolor|#000000
  --#buttonbordercolor|#000000
  --#bodyFontSize|11px
  --#whisper|self
  --#debug|1

--/|==================  Party Funds Report =======================

  --~tokencnt|array;pagetokens;alltokens;@{selected|token_id}

  --/|Loop through all of the tokens in "alltokens" 
  --~TokenId|array;getfirst;alltokens

  --&CharId|[*[&TokenId]:t-represents]

  --=GTtlGP|0 --=TtlCP|0 --=TtlSP|0 --=TtlEP|0 --=TtlGP|0 --=TtlPP|0 

  --?[&TokenId] -eq ArrayError|PF_ENDLOOP

  --&tbl|[t cellpadding="15" border="1" style="width:100%"][tr][td style="width:12%;text-align:left"][b]Name[/b][/td][td style="width:14%;text-align:center"][b]cp[/b][/td][td style="width:14%;text-align:center"][b]sp[/b][/td][td style="width:14%;text-align:center"][b]ep[/b][/td][td style="width:14%;text-align:center"][b]gp[/b][/td][td style="width:13%;text-align:center"][b]pp[/b][/td][td style="width:19%;text-align:center"][b]gp-eqiv[/b][/td][/tr]

  --:PF_LOOPCHECK|
    --&CharId|[*[&TokenId]:t-represents]

    --/|Skip targets that are not on the token layer or that don't represent creatures
    --?"[*[&TokenId]:playercharacter]" -ne 1|PF_CONTINUE

    --&CharId|[*[&TokenId]:t-represents]
    -->SHORT_NAME|[*[&CharId]:character_name]

  --=STtlGP|[*[&CharId]:cp] / 100 
  --=STtlGP|[*[&CharId]:sp] / 10 + [$STtlGP] 
  --=STtlGP|[*[&CharId]:ep] / 2 + [$STtlGP]
  --=STtlGP|[*[&CharId]:gp] + [$STtlGP]
  --=STtlGP|[*[&CharId]:pp]*10 + [$STtlGP]
  --~STtlGP|math;round;[$STtlGP]
  --=GTtlGP|[$GTtlGP] + [$STtlGP]

  --=TtlCP|[*[&CharId]:cp] + [$TtlCP]
  --=TtlSP|[*[&CharId]:sp] + [$TtlSP]
  --=TtlEP|[*[&CharId]:ep] + [$TtlEP]
  --=TtlGP|[*[&CharId]:gp] + [$TtlGP]
  --=TtlPP|[*[&CharId]:pp] + [$TtlPP]

  --&tbl|+ [tr][td style="width:12%;text-align:left"][&gSN][/td][td style="width:14%;text-align:right"][*[&CharId]:cp][/td][td style="width:14%;text-align:right"][*[&CharId]:sp][/td][td style="width:14%;text-align:right"][*[&CharId]:ep][/td][td style="width:14%;text-align:right"][*[&CharId]:gp][/td][td style="width:14%;text-align:right"][*[&CharId]:pp][/td][td style="width:18%;text-align:right"][b][$STtlGP.Total][/b][/td][/tr]

    --:PF_CONTINUE|
    --~TokenId|array;getnext;alltokens
    --?[&TokenId] -ne ArrayError|PF_LOOPCHECK
  --:PF_ENDLOOP|

  --&tbl|+ [tr][td style="width:12%;text-align:right"][b]Total:[/b][/td][td style="width:14%;text-align:right"][b][$TtlCP.Total][/b][/td][td style="width:14%;text-align:right"][b][$TtlSP.Total][/b][/td][td style="width:14%;text-align:right"][b][$TtlEP.Total][/b][/td][td style="width:14%;text-align:right"][b][$TtlGP.Total][/b][/td][td style="width:14%;text-align:right"][b][$TtlPP.Total][/b][/td][td style="width:18%;text-align:right"][b][$GTtlGP.Total][/b][/td][/tr]

  --&tbl|[&tbl] [/t]
  --+|[&tbl]

  --X|

--/|==================  Utility Functions =======================
--:SHORT_NAME|Shortens player name for reporting purposes (6 characters); Parameter:Character_Name
  --~gSN|string;left;6;[%1%]
--<| Return

}}