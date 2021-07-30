!script {{  
  --/|Script Name : Apply Madness
  --/|Version     : 4.0
  --/|Requires SC : 1.3.7+, Mule character sheet with referenced macros installed
  --/|Author      : Will M.

  --/|Description : Wrote for the OOtA campaigns madness component
  --/|              tracks madness in gmnotes and sets an appropriate token status
  --/|              

  --#title|Madness
  --#reentry|Madness
  --#titleCardBackground|#932729
  --#whisper|self
  --#emoteState|1
  --#oddRowBackground|#FFFFFF
  --#evenRowBackground|#FFFFFF
  --#buttonbackground|#FFFFFF
  --#buttontextcolor|#FFFFFF
  --#buttonbordercolor|#FFFFFF
  --#debug|1

  --+[c][#C43112]General[/#][/c]|
  
  --+|[rbutton]🤪⌛::MadST[/rbutton] Short Term (minutes)
  --+|[rbutton]🤪⌛⌛::MadLT[/rbutton] Long Term (hours)
  --+|[rbutton]🤪⌛⌛⌛::MadInd[/rbutton] Indefinite (until cured)

  --+[c][#C43112]👿 Demon Lord 👿[/#][/c]|
  
  --+|[rbutton]👿::MadDL_Bap[/rbutton] Baphomet
  --+|[rbutton]👿::MadDL_Dem[/rbutton] Demogorgon
  --+|[rbutton]👿::MadDL_Fra[/rbutton] Fraz-Urbluu
  --+|[rbutton]👿::MadDL_Gra[/rbutton] Grazzt
  --+|[rbutton]👿::MadDL_Jui[/rbutton] Juiblex
  --+|[rbutton]👿::MadDL_Orc[/rbutton] Orcas
  --+|[rbutton]👿::MadDL_Yee[/rbutton] Yeenoghu
  --+|[rbutton]👿::MadDL_Zug[/rbutton] Zuggtmoy

--X|

--:SHOWMADNESS|TYPE:ST,LT,IND, DL

  --+|[&sMadness]
  --+|[c][i]Roll: [$rMadRoll] [/i][/c]
	--@token-mod| _set statusmarkers|37_cursed::328637:1

  --C[%1%]|ST:ST_SM|LT:LT_SM|IND:IND_SM|DL:DL_SM

  --:ST_SM|
	  --@forselected+|add-cgmnote Short Term Madness([$rMadTime] minutes): [&sMadness]

	--:LT_SM|
  	--@forselected+|add-cgmnote Long Term Madness([$MadTime] hours): [&sMadness]

	--:IND_SM|
	  --@forselected+|add-cgmnote Indefinite Madness: [$sMadness]

	--:DL_SM|
	  --@forselected+|add-cgmnote [&DL] the Demon Lord Madness(Indefinite): [&sMadness]

--X|Should end here 100% of the time 
--<|


--:MadST|
  
  --#title|Short Term Madness
  --=MadTime|1d10
  --#leftsub|Lasts [$MadTime] minutes
  --#whisper|gm, self

  --#oddRowBackground|#CEC7B6
  --#evenRowBackground|#B6AB91
  --#debug|1

  --=rMadRoll|1d100
  --?[$rMadRoll.Total] -le 20|ST_MAD1 
  --?[$rMadRoll.Total] -le 30|ST_MAD2 
  --?[$rMadRoll.Total] -le 40|ST_MAD3 
  --?[$rMadRoll.Total] -le 50|ST_MAD4 
  --?[$rMadRoll.Total] -le 60|ST_MAD5 
  --?[$rMadRoll.Total] -le 70|ST_MAD6 
  --?[$rMadRoll.Total] -le 75|ST_MAD7 
  --?[$rMadRoll.Total] -le 80|ST_MAD8 
  --?[$rMadRoll.Total] -le 90|ST_MAD9|ST_MAD10
  
  --:ST_MAD1| --&sMadness|[b][#1212C4]@{selected|token_name}[/#][/b] retreats into his or her mind and becomes [b][#C43112]paralyzed[/#][/b]. The effect ends if [b][#1212C4]@{selected|token_name}[/#][/b] takes any damage. --^SHOWMADNESS|ST
  --:ST_MAD2| --&sMadness|[b][#1212C4]@{selected|token_name}[/#][/b] becomes incapacitated and spends the duration screaming, laughing, or weeping. --^SHOWMADNESS|ST
  --:ST_MAD3| --&sMadness|[b][#1212C4]@{selected|token_name}[/#][/b] becomes [b][#C43112]frightened[/#][/b] and must use his or her action and movement each round to flee from the source of the fear. --^SHOWMADNESS|ST
  --:ST_MAD4| --&sMadness|[b][#1212C4]@{selected|token_name}[/#][/b] begins babbling and is incapable of normal speech or spellcasting. --^SHOWMADNESS|ST
  --:ST_MAD5| --&sMadness|[b][#1212C4]@{selected|token_name}[/#][/b] must use his or her action each round to attack the nearest creature. --^SHOWMADNESS|ST
  --:ST_MAD6| --&sMadness|[b][#1212C4]@{selected|token_name}[/#][/b] experiences vivid hallucinations and has [b][#C43112]disadvantage[/#][/b] on ability checks. --^SHOWMADNESS|ST
  --:ST_MAD7| --&sMadness|[b][#1212C4]@{selected|token_name}[/#][/b] does whatever anyone tells him or her to do that isn’t obviously self-destructive. --^SHOWMADNESS|ST
  --:ST_MAD8| --&sMadness|[b][#1212C4]@{selected|token_name}[/#][/b] experiences an overpowering urge to eat something strange such as dirt, slime, or offal. --^SHOWMADNESS|ST
  --:ST_MAD9| --&sMadness|[b][#1212C4]@{selected|token_name}[/#][/b] is [b][#C43112]stunned[/#][/b]. --^SHOWMADNESS|ST
  --:ST_MAD10| --&sMadness|[b][#1212C4]@{selected|token_name}[/#][/b] falls [b][#C43112]unconscious[/#][/b]. --^SHOWMADNESS|ST
  -->SHOWMADNESS|ST
--X|

--:MadLT|
  --#title|Long Term Madness
  --#titleCardBackground|#932729
  --=MadTime|1d10 * 10
  --#leftsub|Duration: [$MadTime] hours
  --#whisper|gm, self
  --#emoteState|1
  --#oddRowBackground|#CEC7B6
  --#evenRowBackground|#B6AB91
  --#debug|1

  --=rMadRoll|1d100 
  --?[$rMadRoll.Total] -le 10|LT_MAD1
  --?[$rMadRoll.Total] -le 20|LT_MAD2
  --?[$rMadRoll.Total] -le 30|LT_MAD3
  --?[$rMadRoll.Total] -le 40|LT_MAD4
  --?[$rMadRoll.Total] -le 45|LT_MAD5
  --?[$rMadRoll.Total] -le 55|LT_MAD6
  --?[$rMadRoll.Total] -le 65|LT_MAD7
  --?[$rMadRoll.Total] -le 75|LT_MAD8
  --?[$rMadRoll.Total] -le 85|LT_MAD9
  --?[$rMadRoll.Total] -le 90|LT_MAD10
  --?[$rMadRoll.Total] -le 95|LT_MAD11|LT_MAD12

  --:LT_MAD1| --&sMadness|[b][#1212C4]@{selected|token_name}[/#][/b] feels compelled to repeat a specific activity over and over, such as washing hands, touching things, praying, or counting coins. --^SHOWMADNESS|LT
  --:LT_MAD2| --&sMadness|[b][#1212C4]@{selected|token_name}[/#][/b] experiences vivid hallucinations and has disadvantage on ability checks. --^SHOWMADNESS|LT
  --:LT_MAD3| --&sMadness|[b][#1212C4]@{selected|token_name}[/#][/b] suffers extreme paranoia. [b][#1212C4]@{selected|token_name}[/#][/b] has disadvantage on Wisdom and Charisma checks. --^SHOWMADNESS|LT
  --:LT_MAD4| --&sMadness|[b][#1212C4]@{selected|token_name}[/#][/b] regards something (usually the source of madness) with intense revulsion, as if affected by the antipathy effect of the antipathy/sympathy spell. --^SHOWMADNESS|LT
  --:LT_MAD5| --&sMadness|[b][#1212C4]@{selected|token_name}[/#][/b] experiences a powerful delusion. Choose a potion. [b][#1212C4]@{selected|token_name}[/#][/b] imagines that he or she is under its effects. --^SHOWMADNESS|LT
  --:LT_MAD6| --&sMadness|[b][#1212C4]@{selected|token_name}[/#][/b] becomes attached to a “lucky charm,” such as a person or an object, and has disadvantage on attack rolls, ability checks, and saving throws while more than 30 feet from it. --^SHOWMADNESS|LT
  --:LT_MAD7| --&sMadness|[b][#1212C4]@{selected|token_name}[/#][/b] is blinded (25%) or deafened (75%). --^SHOWMADNESS|LT
  --:LT_MAD8| --&sMadness|[b][#1212C4]@{selected|token_name}[/#][/b] experiences uncontrollable tremors or tics, which impose disadvantage on attack rolls, ability checks, and saving throws that involve Strength or Dexterity. --^SHOWMADNESS|LT
  --:LT_MAD9| --&sMadness|[b][#1212C4]@{selected|token_name}[/#][/b] suffers from partial amnesia. [b][#1212C4]@{selected|token_name}[/#][/b] knows who he or she is and retains racial traits and class features, but doesn’t recognize other people or remember anything that happened before the madness took effect. --^SHOWMADNESS|LT
  --:LT_MAD10| --&sMadness|Whenever [b][#1212C4]@{selected|token_name}[/#][/b] takes damage, he or she must succeed on a DC 15 Wisdom saving throw or be affected as though he or she failed a saving throw against the confusion spell. The confusion effect lasts for 1 minute.. --^SHOWMADNESS|LT
  --:LT_MAD11| --&sMadness|[b][#1212C4]@{selected|token_name}[/#][/b] loses the ability to speak. --^SHOWMADNESS|LT
  --:LT_MAD12| --&sMadness|[b][#1212C4]@{selected|token_name}[/#][/b] falls [b]unconscious[/b]. No amount of jostling or damage can wake [b][#1212C4]@{selected|token_name}[/#][/b]. --^SHOWMADNESS|LT
  --X|

--:MadInd|
  --#title|Indefinite Madness
  --#leftsub|Duration: Until cured
  --#whisper|gm, self
  --#oddRowBackground|#CEC7B6
  --#evenRowBackground|#B6AB91

  --=rMadRoll|1d100

  --?[$rMadRoll.Total] -le 15|IND_MAD1
  --?[$rMadRoll.Total] -le 25|IND_MAD2
  --?[$rMadRoll.Total] -le 30|IND_MAD3
  --?[$rMadRoll.Total] -le 35|IND_MAD4
  --?[$rMadRoll.Total] -le 45|IND_MAD5
  --?[$rMadRoll.Total] -le 50|IND_MAD6
  --?[$rMadRoll.Total] -le 55|IND_MAD7
  --?[$rMadRoll.Total] -le 70|IND_MAD8
  --?[$rMadRoll.Total] -le 80|IND_MAD9
  --?[$rMadRoll.Total] -le 85|IND_MAD10
  --?[$rMadRoll.Total] -le 95|IND_MAD11|IND_MAD12

  --:IND_MAD1| --&sMadness|“Being drunk keeps me sane.” --^SHOWMADNESS|IND
  --:IND_MAD2| --&sMadness|“I keep whatever I find.” --^SHOWMADNESS|IND
  --:IND_MAD3| --&sMadness|“I try to become more like someone else I know — adopting his or her style of dress, mannerisms, and name.” --^SHOWMADNESS|IND
  --:IND_MAD4| --&sMadness|“I must bend the truth, exaggerate, or outright lie to be interesting to other people.” --^SHOWMADNESS|IND
  --:IND_MAD5| --&sMadness|“Achieving my goal is the only thing of interest to me, and I’ll ignore everything else to pursue it.” --^SHOWMADNESS|IND
  --:IND_MAD6| --&sMadness|“I find it hard to care about anything that goes on around me.” --^SHOWMADNESS|IND
  --:IND_MAD7| --&sMadness|“I don’t like the way people judge me all the time.” --^SHOWMADNESS|IND
  --:IND_MAD8| --&sMadness|“I am the smartest, wisest, strongest, fastest, and most beautiful person I know.” --^SHOWMADNESS|IND
  --:IND_MAD9| --&sMadness|“I am convinced that powerful enemies are hunting me, and their agents are everywhere I go. I am sure they’re watching me all the time.” --^SHOWMADNESS|IND
  --:IND_MAD10| --&sMadness|“There’s only one person I can trust. And only I can see this special friend.” --^SHOWMADNESS|IND
  --:IND_MAD11| --&sMadness|“I can’t take anything seriously. The more serious the situation, the funnier I find it.” --^SHOWMADNESS|IND
  --:IND_MAD12| --&sMadness|“I’ve discovered that I really like killing people.” --^SHOWMADNESS|IND
  --X|

--:MadDL_Bap|
  --&DL|Baphomet
  --#title|[&DL] Induced Madness
  --#leftsub|Duration: Until cured
  --#whisper|gm, self
  --#emoteState|1
  --#oddRowBackground|#CEC7B6
  --#evenRowBackground|#B6AB91
  --#debug|1

  --=rMadRoll|1d100
  --?[$rMadRoll.Total] -le 20|DLBAP_MAD1
  --?[$rMadRoll.Total] -le 40|DLBAP_MAD2
  --?[$rMadRoll.Total] -le 60|DLBAP_MAD3
  --?[$rMadRoll.Total] -le 80|DLBAP_MAD4|DLBAP_MAD5

  --+[c][#C43112]Something went wrong!!![/#][/c]|
  --X|

  --:DLBAP_MAD1| --&sMadness|“My anger consumes me. I can’t be reasoned with when my rage has been stoked.” --^SHOWMADNESS|DL
  --:DLBAP_MAD2| --&sMadness|“I degenerate into beastly behavior, seeming more like a wild animal than a thinking being.” --^SHOWMADNESS|DL
  --:DLBAP_MAD3| --&sMadness|“The world is my hunting ground. Others are my prey.” --^SHOWMADNESS|DL
  --:DLBAP_MAD4| --&sMadness|“Hate comes easily to me and explodes into rage.” --^SHOWMADNESS|DL
  --:DLBAP_MAD5| --&sMadness|“I see those who oppose me not as people, but as beasts meant to be preyed upon.” --^SHOWMADNESS|DL
  --X|

--:MadDL_Dem|
  --&DL|Demogorgon
  --#title|[&DL] Induced Madness
  --#leftsub|Duration: Until cured
  --#whisper|gm, self
  --#emoteState|1
  --#oddRowBackground|#CEC7B6
  --#evenRowBackground|#B6AB91
  --#debug|1

  --=rMadRoll|1d100
  --?[$rMadRoll.Total] -le 20|DLDEM_MAD1
  --?[$rMadRoll.Total] -le 40|DLDEM_MAD2
  --?[$rMadRoll.Total] -le 60|DLDEM_MAD3
  --?[$rMadRoll.Total] -le 80|DLDEM_MAD4|DLDEM_MAD5

  --+[c][#C43112]Something went wrong!!![/#][/c]|
  --X|

  --:DLDEM_MAD1| --&sMadness|“Someone is plotting to kill me. I need to strike first to stop them!” --^SHOWMADNESS|DL
  --:DLDEM_MAD2| --&sMadness|“There is only one solution to my problems: kill them all!” --^SHOWMADNESS|DL
  --:DLDEM_MAD3| --&sMadness|“There is more than one mind inside my head.” --^SHOWMADNESS|DL
  --:DLDEM_MAD4| --&sMadness|“If you don’t agree with me, I’ll beat you into submission to get my way.” --^SHOWMADNESS|DL
  --:DLDEM_MAD5| --&sMadness|“I can’t allow anyone to touch anything that belongs to me. They might try to take it away from me!” --^SHOWMADNESS|DL
  --X|

--:MadDL_Fra|
  --&DL|Fraz-Urbluu
  --#title|[&DL] Induced Madness
  --#titleCardBackground|#932729
  --#leftsub|Duration: Until cured
  --#whisper|gm, self
  --#emoteState|1
  --#oddRowBackground|#CEC7B6
  --#evenRowBackground|#B6AB91
  --#debug|1

  --=rMadRoll|1d100
  --#title|Fraz-Urbluu Madness
  --?[$rMadRoll.Total] -le 20|DLFRA_MAD1
  --?[$rMadRoll.Total] -le 40|DLFRA_MAD2
  --?[$rMadRoll.Total] -le 60|DLFRA_MAD3
  --?[$rMadRoll.Total] -le 80|DLFRA_MAD4|DLFRA_MAD5

  --:DLFRA_MAD1| --&sMadness|“I never let anyone know the truth about my actions or intentions, even if doing so would be beneficial to me.” --^SHOWMADNESS|DL
  --:DLFRA_MAD2| --&sMadness|“I have intermittent hallucinations and fits of catatonia.” --^SHOWMADNESS|DL
  --:DLFRA_MAD3| --&sMadness|“My mind wanders as I have elaborate fantasies that have no bearing on reality. When I return my focus to the world, I have a hard time remembering that it was just a daydream.” --^SHOWMADNESS|DL
  --:DLFRA_MAD4| --&sMadness|“I convince myself that things are true, even in the face of overwhelming evidence to the contrary.” --^SHOWMADNESS|DL
  --:DLFRA_MAD5| --&sMadness|“My perception of reality doesn’t match anyone else’s. It makes me prone to violent delusions that make no sense to anyone else.” --^SHOWMADNESS|DL
  --X|

--:MadDL_Gra|
  --&DL|Grazzt
  --#title|[&DL] Induced Madness
  --#leftsub|Duration: Until cured
  --#whisper|gm, self
  --#emoteState|1
  --#oddRowBackground|#CEC7B6
  --#evenRowBackground|#B6AB91
  --#debug|1

  --=rMadRoll|1d100
  --?[$rMadRoll.Total] -le 20|DLGRA_MAD1
  --?[$rMadRoll.Total] -le 40|DLGRA_MAD2
  --?[$rMadRoll.Total] -le 60|DLGRA_MAD3
  --?[$rMadRoll.Total] -le 80|DLGRA_MAD4
  --?[$rMadRoll.Total] -le 90|DLGRA_MAD5|DLGRA_MAD6

  --:DLGRA_MAD1| --&sMadness|“There is nothing in the world more important than me and my desires.” --^SHOWMADNESS|DL
  --:DLGRA_MAD2| --&sMadness|“Anyone who doesn’t do exactly what I say doesn’t deserve to live.” --^SHOWMADNESS|DL
  --:DLGRA_MAD3| --&sMadness|“Mine is the path of redemption. Anyone who says otherwise is intentionally misleading you.” --^SHOWMADNESS|DL
  --:DLGRA_MAD4| --&sMadness|“I will not rest until I have made someone else mine, and doing so is more important to me than my own life—or the lives of others.” --^SHOWMADNESS|DL
  --:DLGRA_MAD5| --&sMadness|“My own pleasure is of paramount importance. Everything else, including social graces, is a triviality.” --^SHOWMADNESS|DL
  --:DLGRA_MAD6| --&sMadness|“Anything that can bring me happiness should be enjoyed immediately. There is no point to saving anything pleasurable for later.” --^SHOWMADNESS|DL
  --X|

--:MadDL_Jui|
  --&DL|Juiblex
  --#title|[&DL] Induced Madness
  --#titleCardBackground|#932729
  --#leftsub|Duration: Until cured
  --#whisper|gm, self
  --#emoteState|1
  --#oddRowBackground|#CEC7B6
  --#evenRowBackground|#B6AB91
  --#debug|1

  --=rMadRoll|1d100

  --?[$rMadRoll.Total] -le 20|DLJUI_MAD1
  --?[$rMadRoll.Total] -le 40|DLJUI_MAD2
  --?[$rMadRoll.Total] -le 60|DLJUI_MAD3
  --?[$rMadRoll.Total] -le 80|DLJUI_MAD4|DLJUI_MAD5

  --:DLJUI_MAD1| --&sMadness|“I must consume everything I can!” --^SHOWMADNESS|DL
  --:DLJUI_MAD2| --&sMadness|“I refuse to part with any of my possessions.” --^SHOWMADNESS|DL
  --:DLJUI_MAD3| --&sMadness|“I’ll do everything I can to get others to eat and drink beyond their normal limits.” --^SHOWMADNESS|DL
  --:DLJUI_MAD4| --&sMadness|“I must possess as many material goods as I can.” --^SHOWMADNESS|DL
  --:DLJUI_MAD5| --&sMadness|“My personality is irrelevant. I am defined by what I consume.” --^SHOWMADNESS|DL
  --X|

--:MadDL_Orc|
  --&DL|Orcas
  --#title|[&DL] Induced Madness
  --#titleCardBackground|#932729
  --#leftsub|Duration: Until cured
  --#whisper|gm, self
  --#emoteState|1
  --#oddRowBackground|#CEC7B6
  --#evenRowBackground|#B6AB91
  --#debug|1

  --=rMadRoll|1d100
  --?[$rMadRoll.Total] -le 20|DLORC_MAD1
  --?[$rMadRoll.Total] -le 40|DLORC_MAD2
  --?[$rMadRoll.Total] -le 60|DLORC_MAD3
  --?[$rMadRoll.Total] -le 80|DLORC_MAD4|MAD5

  --:DLORC_MAD1| --&sMadness|“I often become withdrawn and moody, dwelling on the insufferable state of life.” --^SHOWMADNESS|DL
  --:DLORC_MAD2| --&sMadness|“I am compelled to make the weak suffer.” --^SHOWMADNESS|DL
  --:DLORC_MAD3| --&sMadness|“I have no compunction against tampering with the dead in my search to better understand death.” --^SHOWMADNESS|DL
  --:DLORC_MAD4| --&sMadness|“I want to achieve the everlasting existence of undeath.” --^SHOWMADNESS|DL
  --:DLORC_MAD5| --&sMadness|“I am awash in the awareness of life’s futility.” --^SHOWMADNESS|DL
--X|

--:MadDL_Yee|
  --&DL|Yeenoghu
  --#title|[&DL] Induced Madness
  --#titleCardBackground|#932729
  --#leftsub|Duration: Until cured
  --#whisper|gm, self
  --#emoteState|1
  --#oddRowBackground|#CEC7B6
  --#evenRowBackground|#B6AB91
  --#debug|1

  --=rMadRoll|1d100
  --?[$rMadRoll.Total] -le 20|DLYEE_MAD1
  --?[$rMadRoll.Total] -le 40|DLYEE_MAD2
  --?[$rMadRoll.Total] -le 60|DLYEE_MAD3
  --?[$rMadRoll.Total] -le 80|DLYEE_MAD4|DLYEE_DLBYMad5

  --:DLYEE_MAD1| --&sMadness|“I get caught up in the flow of anger, and try to stoke others around me into forming an angry mob.” --^SHOWMADNESS|DL
  --:DLYEE_MAD2| --&sMadness|“The flesh of other intelligent creatures is delicious!” --^SHOWMADNESS|DL
  --:DLYEE_MAD3| --&sMadness|“I rail against the laws and customs of civilization, attempting to return to a more primitive time.” --^SHOWMADNESS|DL
  --:DLYEE_MAD4| --&sMadness|“I hunger for the deaths of others, and am constantly starting fights in the hope of seeing bloodshed.” --^SHOWMADNESS|DL
  --:DLYEE_MAD5| --&sMadness|“I keep trophies from the bodies I have slain, turning them into adornments.” --^SHOWMADNESS|DL
--X|

--:MadDL_Zug|
  --&DL|Zuggtmoy
  --#title|[&DL] Induced Madness
  --#titleCardBackground|#932729
  --#leftsub|Duration: Until cured
  --#whisper|gm, self
  --#emoteState|1
  --#oddRowBackground|#CEC7B6
  --#evenRowBackground|#B6AB91
  --#debug|1
  --=rMadRoll|1d100

  --?[$rMadRoll.Total] -le 20|DLZUG_MAD1
  --?[$rMadRoll.Total] -le 40|DLZUG_MAD2
  --?[$rMadRoll.Total] -le 60|DLZUG_MAD3
  --?[$rMadRoll.Total] -le 80|DLZUG_MAD4|DLZUG_MAD5

  --:DLZUG_MAD1| --&sMadness|“I see visions in the world around me that others do not.” --^SHOWMADNESS|DL
  --:DLZUG_MAD2| --&sMadness|“I periodically slip into a catatonic state, staring off into the distance for long stretches at a time.” --^SHOWMADNESS|DL
  --:DLZUG_MAD3| --&sMadness|“I see an altered version of reality, with my mind convincing itself that things are true even in the face of overwhelming evidence to the contrary.” --^SHOWMADNESS|DL
  --:DLZUG_MAD4| --&sMadness|“My mind is slipping away, and my intelligence seems to wax and wane.” --^SHOWMADNESS|DL
  --:DLZUG_MAD5| --&sMadness|“I am constantly scratching at unseen fungal infections.” --^SHOWMADNESS|DL
--X|
}}
