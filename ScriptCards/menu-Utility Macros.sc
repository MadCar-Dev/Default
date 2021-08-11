!script {{
  
  --/|Script Name : Utility/Information macros
  --/|Version     : 4.0
  --/|Requires SC : 1.3.7+, Mule character sheet with referenced macros installed
  --/|Author      : Will M.

  --/|Description : A quick menu system agregating a number of useful utility functions
  --/|              Aura, Format Utility, Health, Madness, Lighting, Underdark, 

  --#reentrant|UtilityMacros
  --#title|Utility/Information Macros
  --#titleCardBackground|#932729
  --#titlefontface|Patrick Hand
  --#whisper|self
  --#emoteState|1
  --#oddRowBackground|#FFFFFF
  --#evenRowBackground|#FFFFFF
  --#buttonbackground|#FFFFFF
  --#buttontextcolor|Dark Blue
  --#buttonbordercolor|#FFFFFF
  --#bodyFontSize|11px
  --#debug|0

  --Ssettings|General Macro Tools

  --+[c][#C43112] Macro Setup [/#][/c]|
  --+|[b][rbutton]On::BB;1[/rbutton]/[rbutton]OFF::BB;0[/rbutton][/b] Big Brother
  --+|[button]💓::~Mule|API-HB-On[/button][button]☠️::~Mule|API-HB-Off[/button][button]🚀::~Mule|API-HB-Dev[/button] API Heartbeat On/Off/Dev
  --+|[button]🎰::~Mule|SC-Format-Utility[/button] Scriptcard Format Utility
  --+[c][#C43112]❤️ Health Status ❤️[/#][/c]|
  --+|[button]😴::~Mule|Rest-Short[/button]-[button]🏕️::~Mule|Rest-Long[/button] Rest Short/Long
  --+|[button]🧪::~Mule|Healing-Potion[/button] Healing potion
  --+|[button]❌::~Mule|Kill[/button] Kill token
  --+|[button]🤪::~Mule|Madness[/button] Let's get crazy(Madness)
  --+|[button]➕🤪::~Mule|Madness-Increase[/button][button]➖🤪::~Mule|Madness-Decrease[/button] Inc/Dec Madndess Lvl

  --+[c][#C43112] Light-Saves-Wild Magic [/#][/c]|
  --+|[button]🔥::~Mule|Set-Light[/button] Token Light Settings
  --+|[button]💡::~Mule|Drop-Light-Crumb[/button][button]📜::~Mule|Report-Light-Crumbs[/button] Light Crumb Drop / Report
  --+|[button]✔️::~Mule|GroupCheck[/button][button]🛡️::~Mule|GroupSave[/button] Group [b]Check[/b]/[b]Save[/b]
  --+|[button]✨::~Mule|Wild-Magic-Surge[/button] Wild magic surge

  --+[c][#C43112] Underdark Tables [/#][/c]|
  --+|[button]🍄::~Mule|OOTA-Fungi-of-the-Underdark[/button][button]🗺️::~Mule|OOTA-Underdark-Travel[/button][button]🏃‍♂::~Mule|OOTA-Drow-Pursuit[/button] Fungi/Travel/Pursuit
  --+|[button]📜::~Mule|Z-SM-Supplemental-Tables[/button] Miscellaneous

--x|
--:BB|Mode
  --#hidecard|1
  --@bb|[&reentryval]
  --x|

}}