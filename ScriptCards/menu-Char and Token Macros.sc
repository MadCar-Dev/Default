!script {{  
 
  --/|Script Name : Character-Token Info
  --/|Version     : 4.0
  --/|Requires SC : 1.3.7+, Mule character sheet with referenced macros installed
  --/|Author      : Will M.

  --/|Description : A quick menu system agregating a number of useful character and token macros
  --/|              gmsheet, gmnotes, bios, add Char Note, Set def token, renumber npcs, token notes, ...


  --#title|Character & Token Info
  --#titleCardBackground|#932729
  --#titlefontface|Patrick Hand
  --#whisper|self
  --#emoteState|1
  --#oddRowBackground|#FFFFFF
  --#evenRowBackground|#FFFFFF
  --#buttonbackground|#FFFFFF
  --#buttontextcolor|#000000
  --#buttonFontSize|12px
  --#buttonbordercolor|#FFFFFF
  --#debug|0
  --Ssettings|General Macro Tools

  --+[c][#C43112]Characters[/#][/c]|
  
  --+|[button]&#x1F4DC;See Character [b]Cheat-Sheet[/b]::~Mule|GMSheet[/button] 
  --+|[button]&#x1F4DC; See Character [b]GM Note[/b]::~Mule|Char-GM-Note[/button]
  --+|[button]&#x1F4DC; See Character [b]BIO[/b]::~Mule|Char-Bio[/button]
  --+|[button]&#x1F4DD; Add GM Note::~Mule|Add-GM-CharNote[/button]
  --+|[button]&#x1F9F0; GM Note Config Options::~Mule|gmnote-config[/button]

  --+[c][#C43112] Tokens [/#][/c]|
  --+|[button]&#x2699; Set Default Token::~Mule|Token-SetDefault[/button]
  --+|[button]&#x1F4DC; See Token GM Note::~Mule|Token-GM-Note[/button]
  --+|[button]&#x1F4DD; Add Token GM Note::~Mule|Add-GM-TokenNote[/button]
  --+|[button]&#x1F51F; Renumber Selected NPCs::~Mule|RenumberNPCs[/button]
  --+|[button]&#x1F4CA; Setup NPC Defaults::~Mule|Default-NPC-Token[/button]
}}