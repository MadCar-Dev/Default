var API_Meta = API_Meta || {};
API_Meta.DMDashboard = { offset: Number.MAX_SAFE_INTEGER, lineCount: -1 };
{
  try { throw new Error(''); } catch (e) { API_Meta.DMDashboard.offset = (parseInt(e.stack.split(/\n/)[1].replace(/^.*:(\d+):.*$/, '$1'), 10) - (4)); }
}

// Version 0.6.15
// Last Updated: 5.9.2023
// Purpose: Provides DM/GMs with a set of tools to improve their game management.
//          These tools are based on Handouts programmed to refresh as events occur 
//          in the game and user selections.  
// Handouts Include:
//  * DM Turnorder Report - Primary dashboard, providing a turnorder list with additional
//                          information and functions to make the DM/GM more efficient.
//                          For example, from this handout a DM can toggle a token between
//                          the gmlayer and objects layer, edit a tooltip, make a saving throw
//                          or adjust the tokens HP.
//  * DM Character Sheet -  Works closely with the Turnorder Report.  The content of this 
//                          handout allways contains the character or npc at the top of the
//                          turnorder list.  Most functions, like attacks, checks, saves, 
//                          and spell casting can be peformed from this handout.
//  * DM Notes -            Provides a single location to review all the Notes, Bios, Tooltips
//                          and GM Notes stored in tokens, handouts and character sheets.  
//                          Includes "Favorites" functionality and key word searches. 
//  * DM Turnorder Log -    Contains a CSV data file where each line represents the advancement
//                          of a turn.  Information stored includes start time, end time, and 
//                          details of the character/NPC the turn is associated with.  Plan
//                          to add future trending and reporting against the data collected.
//  * DM Player Access -    Contains table listing all the handouts, characters and tokens (by page)
//                          a player has in his journal and/or can control. 


// To get started, type !DMDash into the chat window to create your initial handouts
// To force the initial build of your DM Notes handout type: !dmnotes --build

// Future Enhancements being cosidered
//  * Add DM Jukebox interface
//    * Tighten up the Favorites area, maybe 3 columns with tight cells (no padding)
//    * Organize into groups Ambiance vs. Sound Effects
//  * Leverage msgbox and html/css class in all my dialogs.
//  * Add Adv/Dis/Normal and Whisper/Public flags? 
//  * Toggle through auras (GM/Player)
//  * Dynamic Lighting Setup
//  * Integrate with !Reporter app to dump predefined reports

/******************************
***     Event Management    ***  
*******************************/
on('ready', () => {
  const version = '0.6.15';

  log('DM Dashboard ' + version + ' is ready! --offset '+ API_Meta.DMDashboard.offset);
  log(' To start using the DM Dashboard, in the chat window enter `!DMDash`');

  API_Meta.DMDashboard.version = version;

  // Check if the namespaced property exists, creating it if it doesn't
  if(!state.DMDashboard) {
      state.DMDashboard = {
          version: version,
          DetailExpand: 0,
          LastDT1: 0,
          LastUTCDate: '',
          DM_Count: 0,
          DM_Avg: 0,
          DM_Secs: 0,
          DM_LastTurn: 0,
          PrevTO: [],
          HPBar: 1,
          charBox_ScrollHeight: '200px',
          toBox_ScrollHeight: '40vh',
          TurnNotification: true,
          DataLogging: false,
          Debug: 0,
          DebugLvl: 4,
          DataLog: '',
          LogChat: false,
          NotesRpt_Tier1MenuSelected: 'Tokens',   // Used to track selection of Notes Report Tier 1 menu
          NotesRpt_SelectedId: 0,  // Used to hold the id of the selected Token/Character/HO in the notes reprot
          NotesRpt_Filter: '',
          NotesRpt_FavsAry: [],
          NotesRpt_FavsOn: 0,
          JB_Tier1MenuSelected: 'Favorites',
          JB_AmbianceFavs: [],
          JB_EffectsFavs: [],
          JB_DashboardBtns: []
      };
  };
 
  state.DMDashboard.version = version;

  if (!state.DMDashboard.charBox_ScrollHeight){ state.DMDashboard.charBox_ScrollHeight = '200px'}
  if (!state.DMDashboard.toBox_ScrollHeight){ state.DMDashboard.toBox_ScrollHeight = '40vh'}
  if (!state.DMDashboard.HPBar){ state.DMDashboard.HPBar = 1;}
  if (!state.DMDashboard.DataLog){ state.DMDashboard.DataLog = '';}
  if (!state.DMDashboard.DataLogging) { state.DMDashboard.DataLogging = false;}
  if (!state.DMDashboard.TurnNotification) { state.DMDashboard.TurnNotification = true;}
  if (!state.DMDashboard.Debug) { state.DMDashboard.Debug = 0;}
  if (!state.DMDashboard.DebugLvl) { state.DMDashboard.DebugLvl = 4;}
  if (!state.DMDashboard.LogChat) { state.DMDashboard.LogChat = false;}
  if (!state.DMDashboard.DM_LastTurn) { state.DMDashboard.DM_LastTurn = 0;}
  if (!state.DMDashboard.NotesRpt_SelectedId) { state.DMDashboard.NotesRpt_SelectedId = 0;}
  if (!state.DMDashboard.NotesRpt_Tier1MenuSelected) { state.DMDashboard.NotesRpt_Tier1MenuSelected = 'Tokens';}
  if (!state.DMDashboard.NotesRpt_Filter) { state.DMDashboard.NotesRpt_Filter = '';}
  if (!state.DMDashboard.hasOwnProperty('NotesRpt_FavsAry')) { 
      state.DMDashboard.NotesRpt_FavsAry = [];
      // log('****** NotesRpt_FavsAry initialized ******');
  }
  if (!state.DMDashboard.NotesRpt_FavsOn) { state.DMDashboard.NotesRpt_FavsOn = 0;}
  if (!state.DMDashboard.hasOwnProperty('JB_AmbianceFavs')) {  
    state.DMDashboard.JB_AmbianceFavs = [];
  }
  if (!state.DMDashboard.hasOwnProperty('JB_EffectsFavs')) {    
    state.DMDashboard.JB_EffectsFavs = [];
  }
  if (!state.DMDashboard.hasOwnProperty('JB_DashboardBtns')) {    
    state.DMDashboard.JB_DashboardBtns = [];
  }

  startPeformanceCheck();
  debounced_DMDash_HandleMsg('!DMDash --TOReport')   
  debounced_DMDash_HandleMsg('!DMNotes --Build')
  debounced_DMDash_HandleMsg('!DMDash --Tracks')

  reportPerformance('Finished On Ready Event');
  
});

on('change:campaign:playerpageid', async () => {
  // log('DM Dashboard Event: change:campaign:turnorder');
  reportPerformance('Start On Change:Campaign:PlayerPagiId event');   
  debounced_DMDash_HandleMsg('!DMNotes --Build')
  reportPerformance('Finished On Change:Campaign:PlayerPagiId event');   
});

on('change:campaign:turnorder', async () => {
  // log('DM Dashboard Event: change:campaign:turnorder');
  reportPerformance('Start change:campaign:turnorder');   
  debounced_DMDash_HandleMsg('!DMDash --TOReport')   
  reportPerformance('Finished change:campaign:turnorder');   

});

on('change:campaign:initiativepage', async () => {
  // log('DM Dashboard Event: change:campaign:initiativepage ' + Campaign().get('initiativepage'));
  if (Campaign().get('initiativepage'))  {
    debounced_DMDash_HandleMsg('!DMDash --SHOW-HO-DIALOG')
  } else {
    debounced_DMDash_HandleMsg('!DMDash --FLUSHDATALOG')
  }

});

on('change:graphic:bar1_value', async () => {
  // log('DM Dashboard Event: change:graphic:bar1_value');
  debounced_DMDash_HandleMsg('!DMDash --TOReport')   
});

on('change:graphic:bar2_value', async () => {
  // log('DM Dashboard Event: change:graphic:bar2_value');
  debounced_DMDash_HandleMsg('!DMDash --TOReport')   
});

on('change:graphic:bar3_value', async () => {
  // log('DM Dashboard Event: change:graphic:bar3_value');
  debounced_DMDash_HandleMsg('!DMDash --TOReport')   
});

on('chat:message', async (msg_orig) => {
  let msg = _.clone(msg_orig);

  
  LogChat(msg)
  if (!/^!DMDash/i.test(msg.content) && !/^!DMNotes/i.test(msg.content)) {
    return;
  }

  // log('HO Event: chat:message');
  reportPerformance(`Start chat:message msg: ${msg.content}`);      
  debounced_DMDash_HandleMsg(msg.content);
  reportPerformance(`End chat:message msg: ${msg.content}`);   

});

function LogChat(msg){
  // Add text to Handout
  if (msg.type != 'api') {

    let logMsg = '';
    if (state.DMDashboard.LogChat == false) {
      return;
    }
    if (msg.type != 'api' && msg.content.length < 1000) {
      logMsg = `${msg.type}: ${msg.who} -> ${msg.target}: ${msg.content}`;
      if (msg.type == 'whisper') {
        logMsg = `<b>${logMsg}</b>`
      }
    } else {
      logMsg = `${msg.type}: ${msg.who} Length: ${msg.content.length}`;
      if (msg.type == 'whisper') {
        logMsg = `<b>${logMsg}</b>`
      }
    }      
    addTextToHandout(logMsg, 'DM Chat Log', 2)
  }
}

// The debounce function helps to optimize performance and prevent excessive or 
// unnecessary function calls, reducing unnecessary processing, improving reponsiveness,
// and avoid potential performance bottlenecks associated with rapid or frequent
// function invocations.
const debounced_DMDash_HandleMsg = _.debounce(DMDash_HandleMsg,250)

// Globals - Need to look into which of these I need to move to state 
let charMap = new Map();
let charMapItem = [];
let foeMap = new Map(); // Stores a Friend/Foe indicator for each token in the TurnOrder
let gStartTime = 0;
let gEndTime = 0;

// ****************************
// *** Global Functions     ***  
// ****************************
function startPeformanceCheck(){
  gStartTime = new Date().getTime();
  gEndTime = gStartTime;
  if (state.DMDashboard.DebugLvl == 5){
    log('Starting Performance Test:' + gStartTime);
  }
}

function reportPerformance(msg){
  let ts = new Date().getTime();
  let stepTime = ts - gEndTime;
  gEndTime = ts;
  let runTime = gEndTime - gStartTime;
  if (state.DMDashboard.DebugLvl == 5){
    log(`${msg} execution time: ${runTime.toFixed(2)}ms. Step time: ${stepTime.toFixed(2)}ms  (Version: ${state.DMDashboard.version})`);
  }
}

function addTextToHandout(noteTxt, handoutName, mode){
  // mode - Determines how txt will be added to the handout
  //  0: Complete replacement
  //  1: Append to bottom
  //  2: Push to the top
  //  3: Append to the bottom and include a timestamp
  //myDebug(4, `addTextToHandout(${handoutName}, mode:${mode}, len:(${noteTxt.length})`)

  const ho = getHandout(handoutName);
  let newText = '';

  // Add the text to the note 
  ho.get("notes", function(notes) {
    switch(mode){
    case 0:
    case '0':
      newText = noteTxt;
      //myDebug(4, `addTextToHandout: Mode 0 (${handoutName}, mode:${mode}, len:(${newText.length})`)        
      break;
    case 1:
    case '1':
      newText = `${notes}<br>${noteTxt}`
      //myDebug(4, `addTextToHandout: Mode 1 (${handoutName}, mode:${mode}, len:(${newText.length})`)        
      break;
    case 2:
    case '2':
      //myDebug(4, `addTextToHandout: Mode 2 (${handoutName}, mode:${mode}, len:(${newText.length})`)
      newText = `${noteTxt}<br>${notes}`
      break;
    case 3:
    case '3':
      //myDebug(4, `addTextToHandout: Mode 3 (${handoutName}, mode:${mode}, len:(${newText.length})`)
      let sysDate = GetSystemUTCDate();
      newText = `${notes}<br>${sysDate}: ${noteTxt}`
      break;
    }
    //myDebug(4, `addTextToHandout: set (${handoutName}, mode:${mode}, len:(${newText.length})`)
    setTimeout(()=>ho.set("notes", newText),0);
  });
}
function getHandout(handoutName) {
  let handout = findObjs({
      _type: 'handout',
      name: handoutName
  })[0];

  if (!handout) {
      handout = createObj('handout', {
          name: handoutName,
          archived: false
      });
  }
  return handout;
}

function DMDash_HandleMsg(msg_content){

  function myDebug(lvl,txt){
    // giDebug: 0 - Off, 1 - API Log, 2 - Chat, 3 - Chat & Log, 4 - Handout (future)
    // giDebugLvl: 0 - All, 1 - Low Info, 2 - High Info, 3 - Basic Debug, 4 - New Code Debug, 5 - Performance logging
  
    if ((state.DMDashboard.Debug == 1 || state.DMDashboard.Debug == 3) && lvl >= state.DMDashboard.DebugLvl) {
      log(txt);
    }
    if ((state.DMDashboard.Debug == 2 || state.DMDashboard.Debug == 3) && lvl >= state.DMDashboard.DebugLvl) {
      sendChat('Debug','/w gm '+txt);
    }
    if (state.DMDashboard.Debug == 4 && lvl >= state.DMDashboard.DebugLvl) {
      // Add to Handout
      addTextToHandout(`${txt} lvl(${lvl}`, "DM Debug Log", 3) 
    }
  }

    // ============================================
    //      PRESENTATION
    // ============================================
    /*
        - color management
        - CSS
        - HTML
        - messaging
        - help construction
    */

    // COLOR MANAGEMENT ===========================
    const getAltColor = (primarycolor, fade = .35) => {

      let pc = hexToRGB(`#${primarycolor.replace(/#/g, '')}`);
      let sc = [0, 0, 0];

      for (let i = 0; i < 3; i++) {
          sc[i] = Math.floor(pc[i] + (fade * (255 - pc[i])));
      }

      return RGBToHex(sc[0], sc[1], sc[2]);
  };
  const RGBToHex = (r, g, b) => {
      r = r.toString(16);
      g = g.toString(16);
      b = b.toString(16);

      if (r.length === 1)
          r = "0" + r;
      if (g.length === 1)
          g = "0" + g;
      if (b.length === 1)
          b = "0" + b;

      return "#" + r + g + b;
  };
  const getTextColor = (h) => {
      h = `#${h.replace(/#/g, '')}`;
      let hc = hexToRGB(h);
      return (((hc[0] * 299) + (hc[1] * 587) + (hc[2] * 114)) / 1000 >= 128) ? "#000000" : "#ffffff";
  };
  const hexToRGB = (h) => {
      let r = 0, g = 0, b = 0;

      // 3 digits
      if (h.length === 4) {
          r = "0x" + h[1] + h[1];
          g = "0x" + h[2] + h[2];
          b = "0x" + h[3] + h[3];
          // 6 digits
      } else if (h.length === 7) {
          r = "0x" + h[1] + h[2];
          g = "0x" + h[3] + h[4];
          b = "0x" + h[5] + h[6];
      }
      return [+r, +g, +b];
  };
  const validateHexColor = (s, d = defaultThemeColor1) => {
      let colorRegX = /(^#?[0-9A-Fa-f]{6}$)|(^#?[0-9A-Fa-f]{3}$)|(^#?[0-9A-Fa-f]{6}\d{2}$)/i;
      return '#' + (colorRegX.test(s) ? s.replace('#', '') : d);
  };

  // CSS ========================================
  const defaultThemeColor1 = '#521e10';
  const defaultThemeColor2 = '#84c6a6';
  const defaultThemeColor3 = '#ffc286';
  const defaultThemeColor4 = '#fff1af';
  const defaultbgcolor = "#ce0f69";

  const defaultdivCSS = {
      "border-radius": `2px`,
      "border": `1px solid #000000`,
      "background-color": '#00000000',
      "overflow": `hidden`
  };
  const defaulttableCSS = {
      'width' : '100%',
      'margin' : '0 auto',
      "border-collapse": 'collapse',
      "font-size": '12px',
  };
  const defaultimgCSS = {};
  const defaultpCSS = {};
  const defaultaCSS = {};
  const defaulth1CSS = {};
  const defaulth2CSS = {};
  const defaulth3CSS = {
    'color': '#521E10'};
  const defaulth4CSS = {
      'color': '#521E10'};
  const defaulth5CSS = {};
  const defaultthCSS = {
      "border-bottom": `1px solid #000000`,
      "font-weight": `bold`,
      "text-align": `center`,
      "line-height": `22px`
  };
  const defaulttrCSS = {};
  const defaulttdCSS = {
      'padding' : '4px',
      'min-width': '10px'
  };
  const defaultcodeCSS = {};

  const defaultMessageHeaderCSS = {
      'border-bottom': `1px solid #000000`,
      'font-weight': `bold`,
      'line-height': `22px`,
      'background-color': '#dedede'
  };
  const defaultMessageBodyCSS = {};
  const defaultButtonCSS = {
      'background-color': defaultThemeColor1,
      'border-radius': '6px',
      'min-width': '25px',
      'padding': '6px 8px'
  };

  const menuBoxCSS = {
    'position': 'fixed',
    'top': '0px',
    'left': '0px',
    'height': '30px',
    'color': '#000', 
    'border': '1px solid #000',
    'background-color': '#FFEBD6',
    'box-shadow': '0 0 2px #000',
    'display': 'block',
    'text-align': 'left',
    'font-size': '13px',
    'padding': '2px',
    'margin-bottom': '2px',
    'font-family': 'sans-serif',
    'white-space': 'pre-wrap'
  }

  const menuButtonCSS = {
    'background-color': '#521E10',
    'border': '1px',
    'color': 'white',
    'text-align': 'center',
    'display': 'inline-block',
    'font-size': '14px',
    'margin': '2px 1px',
    'cursor': 'pointer',
    'padding': '3px 6px',
    'border-radius': '4px'
  };

  const menuButtonCSS_Selected = {
    'background-color': '#521E10',
    'border': '1px',
    'color': 'black',
    'text-align': 'center',
    'display': 'inline-block',
    'font-size': '14px',
    'margin': '2px 1px',
    'cursor': 'pointer',
    'padding': '3px 6px',
    'border-radius': '4px'
  };

  const shadoweddivCSS = {
      'margin': '0px 16px 16px 0px',
      'padding': '5px 5px',
      'box-shadow': '5px 8px 8px #888888'
  };
  const boundingdivCSS = {
      'width' : '100%',
      'border' : 'dashed 0px'
  };
  const imgDivContainer = {
    'width': '50%', // Make the image width responsive to the container's width
    'height': 'auto', // Maintain the image's aspect ratio
    'overflow': 'hidden' // Hide any part of the image that goes beyond the container boundaries
  };
  const img1CSS = {
    'border' : '1px solid black',
  }

  const divC1 = {
    'flex' : '0 0 25%',
    'max-width' : '25%'
  }

  const divC2 = {
    'flex' : '0 0 75%',
    'max-width' : '75%'
  }

  const divC3 = {
    'display' : 'flex',
    'flex-direction' : 'row',
    'align-items' : 'center'
  }

  const divScrollBoxCSS = {
    'height' : '80vh',
    'overflow-y' : 'scroll',
    'border' : '1px solid black',
    'padding' : '5px',
    'display' : 'block'    
  }
  const combineCSS = (origCSS = {}, ...assignCSS) => {
      return Object.assign({}, origCSS, assignCSS.reduce((m, v) => {
          return Object.assign(m, v || {});
      }), {});
  };
  const confirmReadability = (origCSS = {}) => {
      let outputCSS = Object.assign({}, origCSS);
      if (outputCSS['background-color']) outputCSS['background-color'] = validateHexColor(outputCSS['background-color'] || "#dedede");
      if (outputCSS['color'] || outputCSS['background-color']) outputCSS['color'] = getTextColor(outputCSS['background-color'] || "#dedede");
      return outputCSS;
  };
  const assembleCSS = (css) => {
      return `"${Object.keys(css).map((key) => { return `${key}:${css[key]};` }).join('')}"`;
  };
  // HTML =======================================
  const html = {
      div: (content, CSS) => `<div style=${assembleCSS(combineCSS(defaultdivCSS, (CSS || {})))}>${content}</div>`,
      h1: (content, CSS) => `<h1 style=${assembleCSS(combineCSS(defaulth1CSS, (CSS || {})))}>${content}</h1>`,
      h2: (content, CSS) => `<h2 style=${assembleCSS(combineCSS(defaulth2CSS, (CSS || {})))}>${content}</h2>`,
      h3: (content, CSS) => `<h3 style=${assembleCSS(combineCSS(defaulth3CSS, (CSS || {})))}>${content}</h3>`,
      h4: (content, CSS) => `<h4 style=${assembleCSS(combineCSS(defaulth4CSS, (CSS || {})))}>${content}</h4>`,
      h5: (content, CSS) => `<h5 style=${assembleCSS(combineCSS(defaulth5CSS, (CSS || {})))}>${content}</h5>`,
      p: (content, CSS) => `<p style=${assembleCSS(combineCSS(defaultpCSS, (CSS || {})))}>${content}</p>`,
      table: (content, CSS) => `<table style=${assembleCSS(combineCSS(defaulttableCSS, (CSS || {})))}>${content}</table>`,
      th: (content, CSS) => `<th style=${assembleCSS(combineCSS(defaultthCSS, (CSS || {})))}>${content}</th>`,
      tr: (content, CSS) => `<tr style=${assembleCSS(combineCSS(defaulttrCSS, (CSS || {})))}>${content}</tr>`,
      td: (content, CSS) => `<td style=${assembleCSS(combineCSS(defaulttdCSS, (CSS || {})))}>${content}</td>`,
      td2: (content, CSS) => `<td colspan="2" style=${assembleCSS(combineCSS(defaulttdCSS, (CSS || {})))}>${content}</td>`,
      code: (content, CSS) => `<code style=${assembleCSS(combineCSS(defaultcodeCSS, (CSS || {})))}>${content}</code>`,
      a: (content, CSS, link) => `<a href="${link}" style=${assembleCSS(combineCSS(defaultaCSS, (CSS || {})))}>${content}</a>`,
      img:(content, CSS, link, title, width, height) => `<img src="${link}" style=${assembleCSS(combineCSS(defaultimgCSS, (CSS || {})))} alt="${content} width="${width}" height="${height}" title="${title}">`
  }
  // HTML Escaping function
  const HE = (() => {
      const esRE = (s) => s.replace(/(\\|\/|\[|\]|\(|\)|\{|\}|\?|\+|\*|\||\.|\^|\$)/g, '\\$1');
      const e = (s) => `&${s};`;
      const entities = {
          '<': e('lt'),
          '>': e('gt'),
          "'": e('#39'),
          '@': e('#64'),
          '{': e('#123'),
          '|': e('#124'),
          '}': e('#125'),
          '[': e('#91'),
          ']': e('#93'),
          '"': e('quot')
      };
      const re = new RegExp(`(${Object.keys(entities).map(esRE).join('|')})`, 'g');
      return (s) => s.replace(re, (c) => (entities[c] || c));
  })();
  // MESSAGING ==================================
  const btnAPI = ({ api: api = "", label: btnlabel = "Run API", css: css = defaultButtonCSS } = {}) => {
      let btnCSS = confirmReadability(css);
      //r20css = {};
      //if(['t', 'true', 'y', 'yes', true].includes(r20style)) Object.assign(r20css, {padding: '5px', display: 'inline-block', border: '1px solid white;'});
      return html.a(btnlabel, btnCSS, HE(api));
  };
  const msgbox = ({
      msg: msg = "message",
      title: title = "DMDash Output",
      btn: btn = "buttons",
      sendas: sendas = "API",
      whisperto: whisperto = "",
      headercss: headercss = {},
      bodycss: bodycss = {}
  }) => {
      let hdrCSS = confirmReadability(combineCSS(defaultMessageHeaderCSS, headercss));
      let bodyCSS = confirmReadability(combineCSS(defaultMessageBodyCSS, bodycss));

      let hdr = html.tr(html.td(title, {}), hdrCSS);
      let body = html.tr(html.td(msg, {}), bodyCSS);
      let buttons = btn !== "buttons" ? html.tr(html.td(btn, { 'text-align': `right`, 'margin': `4px 4px 8px`, 'padding':'8px' }), {}) : "";

      let output = html.div(html.div(html.table(`${hdr}${body}${buttons}`, {}), shadoweddivCSS), boundingdivCSS);
      if (whisperto) output = `/w "${whisperto}" ${output}`;
      sendChat(sendas, output);
  };
  function dmdash_ConfigSettings(){
    msgbox({
      msg: html.table(
          html.tr(html.th(`ARG`, { 'text-align': 'left', 'min-width':'70px' }) + html.th(`Current Value`, { 'text-align': 'left' })) +
          html.tr(html.td(`version:`) + html.td(`${state.DMDashboard.version}`)) +
          html.tr(html.td(`DetailExpand:`) + html.td(`${state.DMDashboard.DetailExpand}`)) +
          html.tr(html.td(`LastDT1:`) + html.td(`${state.DMDashboard.LastDT1}`)) +
          html.tr(html.td(`LastUTCDate:`) + html.td(`${state.DMDashboard.LastUTCDate}`)) +
          html.tr(html.td(`DM_Count:`) + html.td(`${state.DMDashboard.DM_Count}`)) +
          html.tr(html.td(`DM_Avg:`) + html.td(`${state.DMDashboard.DM_Avg}`)) +
          html.tr(html.td(`DM_Secs:`) + html.td(`${state.DMDashboard.DM_Secs}`)) +
          html.tr(html.td(`HPBar:`) + html.td(`${state.DMDashboard.HPBar}`)) +
          html.tr(html.td(`charBox_ScrollHeight:`) + html.td(`${state.DMDashboard.charBox_ScrollHeight}`)) +
          html.tr(html.td(`toBox_ScrollHeight:`) + html.td(`${state.DMDashboard.toBox_ScrollHeight}`)) +
          html.tr(html.td(`TurnNotification:`) + html.td(`${state.DMDashboard.TurnNotification}`)) +
          html.tr(html.td(`DataLogging:`) + html.td(`${state.DMDashboard.DataLogging}`)) +
          html.tr(html.td(`Debug:`) + html.td(`${state.DMDashboard.Debug}`)) +
          html.tr(html.td(`DebugLvl:`) + html.td(`${state.DMDashboard.DebugLvl}`))), 
        headercss: { 'background-color': defaultThemeColor1 },
        title: `DMDash State Variables`,
        whisperto: `GM`
    });
  }
  function dmdash_Help(){
    msgbox({
      msg: html.h2(`INTRODUCTION`) +
          html.p(`DMDash is a DM/GM tool to that provides a heads-up display in for managing combat.`) +
          html.h2(`Basic Commands:`) +
          html.h3(`DM Dash: !DMDash [cmd]`) +
          html.table(
              html.tr(html.th(`ARG`, { 'text-align': 'left', 'min-width':'70px' }) + html.th(`EXPLANATION`, { 'text-align': 'left' })) +
              html.tr(html.td(`--debug output level`) + html.td(`Turns on debug messaging. <b>Output:</b> 0:Off 1:API/Log 2:Chat 3:Chat & Log 4:Handout  <b>Level</b> 0:All 1:Low Info 2: High Info 3: Basic Debug 4:New Code Debug 5: Performance`)) +
              html.tr(html.td(`--notifications (0/1)`) + html.td(`Turns on/off turn player turn notifications.`)) +
              html.tr(html.td(`--dataloggin (0/1)`) + html.td(`Turns on/off data logging`)) +
              html.tr(html.td(`--hpbar (1/2/3)`) + html.td(`Sets the token bar (1, 2, or 3) to be used for HP display.`)) +
              html.tr(html.td(`--sbheight (CB Height) (TO Height)`) + html.td(`Sets the height of the Character Detail and Turnorder areas of the dashboard.`)) +
              html.tr(html.td(`--playerstats`) + html.td(`Report player time statistics.`)) +
              html.tr(html.td(`--clearcache`) + html.td(`Clear memory cache, forcing character details to refresh.`)) 
          ) +
          html.h4(`DM Notes: !DMNotes [cmd]`) +
          html.table(
              html.tr(html.th(`ARG`, { 'text-align': 'left', 'min-width':'70px' }) + html.th(`EXPLANATION`, { 'text-align': 'left' })) +
              html.tr(html.td(`--build`) + html.td(`Forces a new DM Note to be created`)) 
          ) +
          html.h3(`EXAMPLES`) +
          html.table(
              html.tr(html.td2(`!DMDash --hpbar 3`, { 'font-weight': 'bold' })) +
              html.tr(html.td(`&nbsp;`) + html.td(`> Tells DM Dashboard to use bar3_value for tracking HP`, { 'font-style': 'italic' })) +
              html.tr(html.td2(`!DMDash --dataloggin 1`, { 'font-weight': 'bold' })) +
              html.tr(html.td(`&nbsp;`) + html.td(`> Turns on data logging to DM Turnorder Log handout`, { 'font-style': 'italic' })) +
              html.tr(html.td2(`!DMNotes --build`, { 'font-weight': 'bold' })) +
              html.tr(html.td(`&nbsp;`) + html.td(`> Build a new DM Notes handout, or create a new one if it didn't previously exist.`, { 'font-style': 'italic' }))),
      headercss: { 'background-color': defaultThemeColor1 },
      title: `DMDash HELP`,
      whisperto: `GM`
  });

  }
  function showPlayerStats(bShowPlayers){

    let pcs = findObjs({
      type: 'character'
    }).sort((a, b) => (a.get("represents") > b.get("represents") ? 1 : -1));

    let msg = html.tr(html.th('Name') + html.th('Avg') + html.th('Secs') + html.th('Count') + html.th('Last'));
    pcs.forEach(c => {
      if (c.get("controlledby") !== ''){
        let pc_name = c.get('name');
        let to_avg = getAttrByName(c.get('_id'), 'to_avg');
          if (to_avg != undefined){
            let to_secs = getAttrByName(c.get('_id'), 'to_secs');
            let to_count = getAttrByName(c.get('_id'), 'to_count');
            let to_lastturn = getAttrByName(c.get('_id'), 'to_lastturn');
            msg += html.tr(html.td(pc_name) + html.td(to_avg) + html.td(to_secs) + html.td(to_count) + html.td(to_lastturn));
          }
      }
    });
    msg += html.tr(html.td('DM') + html.td(state.DMDashboard.DM_Avg) + html.td(state.DMDashboard.DM_Secs) + html.td(state.DMDashboard.DM_Count) + html.td(state.DMDashboard.DM_LastTurn));
    msg = html.table(msg);

    if (bShowPlayers == 1){
      msgbox({
        msg: msg,
        headercss: { 'background-color': defaultThemeColor1 },
        title: `Player Stats`
      });
    } else {
      let api = `!DMDash --playerstats 1`;
      let btn = btnAPI({api: api, label: `Show to players`});
      msgbox({
        msg: msg,
        headercss: { 'background-color': defaultThemeColor1 },
        title: `Player Stats`,
        whisperto: `GM`,
        btn: btn
      });
    }
  }
  function flushDataLog(){
    if (state.DMDashboard.DataLog.length == 0 || state.DMDashboard.DataLogging == false) { 
      return; 
    }
    addTextToHandout(state.DMDashboard.DataLog, "DM Turnorder Log", 1);
    state.DMDashboard.DataLog = '';
  }
  // Debug routines to dump out map and object contents
  function dumpMapObject(map) {
    for (let [key, value] of map) {
      myDebug(3,`Key: ${key}`);
      dumpObject(value);
    }
  }
  function dumpObject(obj){
    for (let key in obj) {
      if (obj.hasOwnProperty(key)) {
        myDebug(3,`  Field Name: ${key} => ${obj[key]}`);
      }
    }
  }
  function interpolate(x, dataset) {
    if (x <= dataset[0][0]) {
      return dataset[0][1];
    }
    if (x >= dataset[dataset.length - 1][0]) {
      return dataset[dataset.length - 1][1];
    }
    for (let i = 0; i < dataset.length - 1; i++) {
      if (x >= dataset[i][0] && x <= dataset[i + 1][0]) {
        const x1 = dataset[i][0];
        const y1 = dataset[i][1];
        const x2 = dataset[i + 1][0];
        const y2 = dataset[i + 1][1];
        const interpolatedValue = y1 + ((x - x1) * (y2 - y1)) / (x2 - x1);
        return interpolatedValue;
      }
    }
  }
  function createNoteLog() {
    const noteLog = createObj('handout',{
      name: 'DM Turnorder Log'
    });
    noteLog.set('notes', '<h3>Turnorder Log</h3>');
    return noteLog;
  };
  function getNoteLog() {
    const noteLog = filterObjs(function(o){
      return ( 'handout' === o.get('type') && 'DM Turnorder Log' === o.get('name') && false === o.get('archived'));
    })[0];

    if(noteLog) {
      return noteLog;
    } 
    return createNoteLog();
  };
  function updateTurnOrderStartTime(){
    state.DMDashboard.LastDT1 = getSystemTimeInSecs();
    state.DMDashboard.LastUTCDate = GetSystemUTCDate();
  }
  function myGetAttrByName(character_id,
                           attribute_name,
                           attribute_default_current,
                           attribute_default_max,
                           value_type) {
      attribute_default_current = attribute_default_current || '';
      attribute_default_max = attribute_default_max || '';
      value_type = value_type || 'current';

      var attribute = findObjs({
          type: 'attribute',
          characterid: character_id,
          name: attribute_name
      }, {caseInsensitive: true})[0];
      if (!attribute) {
          attribute = createObj('attribute', {
              characterid: character_id,
              name: attribute_name,
              current: attribute_default_current,
              max: attribute_default_max
          });
      }

      if (value_type == 'max') {
          return attribute.get('max');
      } else {
          return attribute.get('current');
      }
  }
  function DidTOAdvance(){
    let cmd_advance = 0; //Boolean value where next_cmd = 1 means TO changed as a result of advancing the TO

    let prev_to = state.DMDashboard.PrevTO;
    let curr_to = Campaign().get('turnorder');

    // log(`DidTOAdvance: Prev:${prev_to} Curr:${curr_to}`)
    state.DMDashboard.PrevTO = curr_to;

    if (prev_to.length = 0) {
      return 0;
    }
    
    if (!prev_to || !curr_to){
      return 0;
    }

    if (!prev_to || !curr_to || prev_to == '' || curr_to == '') {
        // log('TO Change: Exit-Empty TurnOrder');
        return 0;
    }

    // take the JSON string and convert it to an object
    prev_to_json = JSON.parse(prev_to);
    curr_to_json = JSON.parse(curr_to);
    // log('TO Change: Test Lengths(curr vs. prev): ' + curr_to_json.length + ' / ' + prev_to_json.length);

    if (prev_to_json.length == 0 || curr_to_json.length == 0){
      return 0;  
    }
    
    if (prev_to_json.length == curr_to_json.length) {

      // log('TO Change: Same Length');
      //Shift the prev_to and see if is now equivalent to the curr_to    
      prev_to_json.push(prev_to_json.shift());
      // Update any formulas
      if (prev_to_json[0].id == -1 && prev_to_json[0].formula && prev_to_json[0].formula !== '') {
              prev_to_json[0].pr = prev_to_json[0].pr + eval(prev_to_json[0].formula);
      }

      if (JSON.stringify(prev_to_json) == curr_to) {
        // If they are equivalent, then the only change was because the turn changed
        // log('TO Change: TOs are equal: ');
        cmd_advance = 1;
        AddToTurnorderLog();
      } else {
        // log('TO Change: TOs are not equal:');
        // log('Prev_To:' + JSON.stringify(prev_to_json));
        // log('Curr_To:' + curr_to);
      }
    }
    // log('DidTOAdvance: ' + cmd_advance);
    return cmd_advance;  
  };
  function createNoteLog() {
    const noteLog = createObj('handout',{
      name: 'DM Turnorder Log'
    });
    noteLog.set('notes', '<h3>Turnorder Log</h3>');
    return noteLog;
  };
  function getNoteLog() {
    const noteLog = filterObjs(function(o){
      return ( 'handout' === o.get('type') && 'DM Turnorder Log' === o.get('name') && false === o.get('archived'));
    })[0];

    if(noteLog) {
      return noteLog;
    } 
    return createNoteLog();
  };
  function GetSystemUTCDate() {
    let d = new Date();
    let utcDate = `${d.getUTCMonth().toString().padStart(2,'0')}/${d.getUTCDate().toString().padStart(2,'0')}/${d.getUTCFullYear()} ${d.getUTCHours().toString().padStart(2,'0')}:${d.getUTCMinutes().toString().padStart(2,'0')}:${d.getUTCSeconds().toString().padStart(2,'0')}`;
    return utcDate;
  };
  function AddToTurnorderLog(){

    if (state.DMDashboard.DataLogging == false){
      return;
    }
    // return;
  // Purpose:  Write a csv record to a handout called 
    let toChar = [];
    let toObj = [];
    let toToken = [];
    let NewData = '';
    let repeatingSection = '';  //Prefix
    let repeatingName = '';      //Suffix 
    let repeatingValues = [];
    let cName = '';
    let cHP = 0;
    let cHPMax = 0
    let cLevel = '';
    let cClass = '';
    let cRace = '';
    let cCasterLevel = '';
    let cAtkCnt = 0;
    let cTraitCnt = 0;
    let tType = '';

    let curr_to = Campaign().get("turnorder");
    if (curr_to === '') {
      return;
    }

    //Get the Turn Order object into a Json object
    toObj = JSON.parse(curr_to);

    //Get the Current System Time
    let currDate = GetSystemUTCDate();

    //Get Last System Time state.DMDashboard.LastDT1
    let prevDate = state.DMDashboard.LastUTCDate;

    // log('TO:1');
    // What is this turnorder tied to?  A pc, npc, custom item or other?
    tType = getTokenType(toObj[toObj.length-1].id); // Returns NPC, CHAR, CUSTOM, UTILITY or OTHER
    //log('TO?:' + tType);

    switch (tType) {

      case 'CUSTOM':
        NewData = `CUSTOM,${prevDate},${currDate},${toObj[toObj.length-1].id},${toObj[toObj.length-1].pr},${toObj[toObj.length-1].custom},0,0,0,0,0,0,0`
        break;

      case 'CHAR':
      case 'NPC-CHARSHEET':
      
        // TokenType, Start, End, 'Char', TurnOrderId, pr, CharacterName, 0, Level, Class, Race, Attacks_Count, Traits_Count, Spells_Count
        toToken = getObj("graphic", toObj[toObj.length-1].id);
        toChar = getObj("character", toToken.get('represents'));
        cName = toChar.get('name')
        cLevel = getAttrByName(toChar.get('_id'),'level','current');
        cClass = getAttrByName(toChar.get('_id'),'class_display','current');
        cRace = getAttrByName(toChar.get('_id'),'race_display','current');
        cCasterLevel = getAttrByName(toChar.get('_id'),'caster_level','current');
        cHP = toToken.get(`bar${state.DMDashboard.HPBar}_value`);
        cHPMax = toToken.get(`bar${state.DMDashboard.HPBar}_max`);

        repeatingSection = 'repeating_attack';  //Prefix
        repeatingName = 'atkname';              //Suffix 
        repeatingValues = findObjs({_type: "attribute", _characterid: toChar.get('_id')})
          .filter(attribute => attribute.get("name").startsWith(`${repeatingSection}_`) && attribute.get("name").endsWith(`_${repeatingName}`));
        cAtkCnt = repeatingValues.length
          
        repeatingSection = 'repeating_traits';  //Prefix
        repeatingName = 'name';                 //Suffix 
        repeatingValues = findObjs({_type: "attribute", _characterid: toChar.get('_id')})
          .filter(attribute => attribute.get("name").startsWith(`${repeatingSection}_`) && attribute.get("name").endsWith(`_${repeatingName}`));
        cTraitCnt = repeatingValues.length
        if (tType = 'CHAR'){
          NewData = `CHAR,${prevDate},${currDate},"'${toObj[toObj.length-1].id}",${toObj[toObj.length-1].pr},"${cName}",0,"${cLevel}","${cClass}","${cRace}",${cAtkCnt},${cTraitCnt},${cCasterLevel},${cHP},${cHPMax}`          
        } else {
          NewData = `NPC,${prevDate},${currDate},"'${toObj[toObj.length-1].id}",${toObj[toObj.length-1].pr},"${cName}",0,"${cLevel}","${cClass}","${cRace}",${cAtkCnt},${cTraitCnt},${cCasterLevel},${cHP},${cHPMax}`
        }

        break;

      case 'NPC':

        // TokenType, Start, End, 'NPC', TurnOrderId, pr, Name, 0, CR, Type, SubType, Attack_Count(A, RA, BA, LA), Traits_Count, Spells_Count
        toToken = getObj("graphic", toObj[toObj.length-1].id);
        toChar = getObj("character", toToken.get('represents'));
        cName = toChar.get('name')
        cLevel = getAttrByName(toChar.get('_id'),'npc_challenge','current');
        cClass = getAttrByName(toChar.get('_id'),'npc_type','current');
        cRace = ''
        cCasterLevel = getAttrByName(toChar.get('_id'),'caster_level','current');
        cHP = toToken.get(`bar${state.DMDashboard.HPBar}_value`);
        cHPMax = toToken.get(`bar${state.DMDashboard.HPBar}_max`);

        repeatingSection = 'repeating_npcaction';  //Prefix
        repeatingName = 'name'                    //Suffix 
        repeatingValues = findObjs({_type: "attribute", _characterid: toChar.get('_id')})
          .filter(attribute => attribute.get("name").startsWith(`${repeatingSection}_`) && attribute.get("name").endsWith(`_${repeatingName}`));
        cAtkCnt = repeatingValues.length

        repeatingSection = 'repeating_npctrait';  //Prefix
        repeatingName = 'name'                    //Suffix 
        repeatingValues = findObjs({_type: "attribute", _characterid: toChar.get('_id')})
          .filter(attribute => attribute.get("name").startsWith(`${repeatingSection}_`) && attribute.get("name").endsWith(`_${repeatingName}`));
        cTraitCnt = repeatingValues.length

        //         NPC, Start,       End,       TurnOrderId,               pr,                         Name,    0,  Level,    Class,   N/A,      Attacks,   Traits,      CasterLvl
        NewData = `NPC,${prevDate},${currDate},"'${toObj[toObj.length-1].id}",${toObj[toObj.length-1].pr},"${cName}",0,"${cLevel}","${cClass}","${cRace}",${cAtkCnt},${cTraitCnt},${cCasterLevel}`
        break;

      case 'OTHER':
        //         OTHER, Start,       End,       TurnOrderId,               pr,                         Name,    0,  Level,    Class,   N/A,      Attacks,   Traits,      CasterLvl
        // Start, End, 'Other', TurnOrderId, pr, TokenName, 0,0,0,0,0,0,0,0,0,
        // log('TO:OTHER');
        toToken = getObj("graphic", toObj[toObj.length-1].id);
        cName = toToken.get('name');
        NewData = `OTHER,${prevDate},${currDate},"'${toObj[toObj.length-1].id}",${toObj[toObj.length-1].pr},${cName},0,0,0,0,0,0,0`
        break;
    }

    // Build up the datalog over time, flush it to a handout (DM Turnorder Log)  when the Roll20 Turnorder is closed
    state.DMDashboard.DataLog += '<br>' + NewData;
  };
  function tokenToggleVisibility(tId){
    const token = getObj('graphic', tId);
    if (!token){
      return;
    }

    if (token.get('layer')=='objects'){
      token.set('layer', 'gmlayer');
    } else {
      token.set('layer', 'objects');
    }
  }
  function tokenAdjHP(tId, adjHP){
    //If there is a '+'' or '-' in front of HP, will adjust hp
    //  Otherwise, it will set it.
    // log(`tokenAdjHP1: tId: ${tId} AdjHP:${adjHP}`)    
    
    const token = getObj('graphic', tId);
    if (!token){
      return;
    }
    let hp = adjHP.trimStart().trimEnd();

    // log(`tokenAdjHP2: tId: ${tId} AdjHP:${adjHP} hp:${hp}`)

    if (hp[0] == '+' || hp[0] == '-'){
      hp = Number(token.get(`bar${state.DMDashboard.HPBar}_value`)) + Number(hp)
      token.set(`bar${state.DMDashboard.HPBar}_value`, hp);
    } else {
      token.set(`bar${state.DMDashboard.HPBar}_value`, hp);
    }
  }
  function tokenSetTooltip(tId, textToAdd){
    const token = getObj('graphic', tId);
    if (!token){
      return;
    }
    // log(`tokenSetTooltip: tId:${tId} Text:${textToAdd}`);
    token.set('tooltip', textToAdd);
  }
  function tokenClearTooltip(tId){
    const token = getObj('graphic', tId);
    if (!token){
      return;
    }
    token.set('tooltip', '');
  }
  function tokenToggleTooltip(tId){
    const token = getObj('graphic', tId);
    if (!token){
      return;
    }

    if (token.get('show_tooltip')){
      token.set('show_tooltip',false);
    } else {
      token.set('show_tooltip',true);
    }
  }
  function tokenToggleLock(tId){
    const token = getObj('graphic', tId);
    if (!token){
      return;
    }

    if (token.get('lockMovement')){
      token.set('lockMovement',false);
    } else {
      token.set('lockMovement',true);
    }
  }
  function tokenToggleNameplate(tId){
    const token = getObj('graphic', tId);
    if (!token){
      return;
    }
    if (token.get('showplayers_name')){
      token.set('showplayers_name',false);
    } else {
      token.set('showplayers_name',true);
    }
  }
  function toggleFriendFoe(tId){

    // foeMap (toObj[0].id, 
    //          {id: toObj[0].id, 
    //           charId: toChar.get('id'), 
    //           Type: 'NPC', 
    //           State:'FOE';, 
    //           Level: edCharLevel, 
    //           SpellCasterLvl: getAttrByName(toChar.get('_id'), 'caster_level') ,
    //           Exp: edNPCExp});

    myDebug(2,`toggleFriendFoe(${tId})`)
    //dumpMapObject(foeMap);

    if (foeMap.has(tId)) {

      // Yes - use the map data (as it might change when toggled)
      let foeItem = foeMap.get(tId);
      myDebug(2, `ToggleFriendFoe: Found id: ${foeItem.id} State: ${foeItem.State} Type: ${foeItem.Type}`);

      if (foeItem.Type == 'CHAR') { //CHAR: (Friend->Neutral->Foe->Friend)
        if (foeItem.State == 'FRIEND') {
          foeItem = { ...foeItem, State: 'NEUTRAL'};
        } else if (foeItem.State == 'NEUTRAL') {
          foeItem = { ...foeItem, State: 'FOE'};
        } else {
          foeItem = { ...foeItem, State: 'FRIEND'};
        }
      } else if (foeItem.Type == 'NPC') { //NPC:(Foe->Neutral->Friend->Foe)
        if (foeItem.State == 'FOE') {
          foeItem = { ...foeItem, State: 'NEUTRAL'};
        } else if (foeItem.State == 'NEUTRAL') {
          foeItem = { ...foeItem, State: 'FRIEND'};
        } else {
          foeItem = { ...foeItem, State: 'FOE'};
        }
      }
      foeMap.set(tId, foeItem);
    }
  }
  function addTextToCharGMNote(characterId, text){
    const char = getObj('character', characterId);
    if (!char) {
      sendChat('API', `/w gm Character with ID ${characterId} not found.`);
      return;
    }

    char.get('gmnotes', function(n){
      if(!_.isNull(n)){
        setTimeout(function(){
          let newText=n+'<br>' + text;
          char.set('gmnotes',newText);                        
        },0);
      }
    });
  }
  const addTextToTokenGMNote = async (tokenId, textToAdd) => {
    const token = getObj('graphic', tokenId);

    if (!token) {
      sendChat('API', `/w gm token with ID ${tokenId} not found.`);
      return;
    }

    const currentNotes = token.get('gmnotes');
    const updatedNotes = currentNotes === 'null' ? textToAdd : `${currentNotes}<br>${textToAdd}`;

    // Update the bio field asynchronously using a Promise
    return new Promise((resolve) => {
      token.set('gmnotes', updatedNotes, (err) => {
        if (err) {
          sendChat('API', `/w gm Error updating token notes: ${err.message}`);
          resolve(false);
        } else {
          sendChat('API', `/w gm Successfully added text to token notes for character ${token.get('name')}.`);
          resolve(true);
        }
      });
    });
    // log(`AddTextToTokenGMNote: tokenId:${tokenId} text:${textToAdd} currText:${currentNotes} newText:${updatedNotes}`)
  };
  const addTextToGMNote = async (textToAdd) => {
    const ho = getHandout('DM Turnoder List');

    if (!ho) {
      sendChat('API', `/w gm handout "DM Turnoder List" not found.`);
      return;
    }

    ho.get('gmnotes', function(n){
      if(!_.isNull(n)){
        setTimeout(function(){
          let text=textToAdd + '<br>'+ n;
          ho.set('gmnotes',text);                        
        },0);
      }
    });

    sendChat('API', `/w gm Tried to add "${textToAdd}" to gmnotes `);

  };
  function showGMNote(tId, flag) {
    //Flag - Determines what to return
    // 0: For NPC type, Token GM Note
    //    For PC T type, Character Sheet GM Note
    // 1: Forces the Token GM Note
    // 2: Forces the Charactr GM Note
    // 3: Gets Both

    let tType = getTokenType(tId)
    let doToken = false;
    let doChar = false;
    let doBoth = false;
    let gmnotes = '';
    let tgmnotes = '';
    let cgmnotes = '';


    var tObj = getObj('graphic', tId);
    if (!tObj) {
      return;
    } else if ((flag == 0 && tType == 'NPC') || flag == 1 || flag == 3) {
      doToken = true;
    }
    var cObj = getObj('character', tObj.get('represents'));
    if (cObj){
      if ((flag == 0 && tType == 'CHAR') || flag == 2 || flag ==3) {
        doChar = true;
      }  
    }
    if (doChar && doToken) {
      doBoth = true;
      doToken = false;
      doChar = false;
    } 

    myDebug(3, `showGMNote: tId:${tId} flag:${flag} doToken:${doToken} doChar:${doChar} doBoth:${doBoth}`)

    if (doToken==true) {

      let handout = getHandout('DM Notes');
      //gmnotes =tObj.get('gmnotes');
      gmnotes = unescape(decodeUnicode(tObj.get('gmnotes')));
      myDebug(3, `showGMNote(Token): gmnotes: ${gmnotes}`);
      setTimeout(()=>handout.set("notes", gmnotes),500);

    }

    if (doChar==true) {
      cObj.get('gmnotes', function(gmnotes){
        myDebug(3, `showGMNote(Char): gmnotes:${gmnotes}`);
        let handout = getHandout('DM Notes');
        setTimeout(()=>handout.set("notes", gmnotes),500);
      });  
    }

    if (doBoth==true) {
      tObj.get('gmnotes', function(tgmnotes){
        cObj.get('gmnotes', function(cgmnotes){
          let notes = `<h2>Token GM Notes</h2>\n${tgmnotes}<hr>\n<h2>Character GM Notes</h2>\n${cgmnotes}`
          myDebug(3, `showGMNote(Both): gmnotes:${notes}`)          
          setTimeout(()=>handout.set("notes", notes),500);

        });  
      });
    }
  }
  function showAvatar(tId, bTitle, bWhisper) {
    // bTitle, 0: No Title, 1: Title(In Template Box) 2: Just Image
    let msgPrefix = '';
    let msgSendToPlayers = '';
    let msg = '';
    if (bWhisper == 0){bWhisper = false} else {bWhisper = true}

    myDebug(3, `ShowAvatar: ${tId}, ${bTitle}, ${bWhisper}`)

    let tObj = getObj('graphic', tId);
    if (tObj) {
      let cObj = getObj('character', tObj.get('represents'));
      if (cObj){
        let avatar = `<img src='${cObj.get('avatar')}'>`;
        let name = '';
        if (bTitle==1){
          name = cObj.get('name');  
        }

        let msgPrefix = '';
        if (bWhisper){
          msgPrefix='/w gm '
          msgSendToPlayers = `\n[Send to Players: With Title](!DMDash --SHOWAVATAR ${tId} 1 0) \n[Send to Players: No Title](!DMDash --SHOWAVATAR ${tId} 0 0) \n[Send to Players: No Frame](!DMDash --SHOWAVATAR ${tId} 2 0)`
        }
        myDebug(3, `ShowAvatar: Avatar ${avatar}`)
        myDebug(3, `ShowAvatar: name ${name}`)

        if (bTitle != 2){
          msg = `${msgPrefix}&{template:npcaction}{{rname=${name}}} {{description=${avatar} ${msgSendToPlayers}}}`;
        } else {
          msg = avatar
        }
        sendChat(name, msg);
        myDebug(3, msg)

      }
    }
  }
  const decodeUnicode = (str) => str.replace(/%u[0-9a-fA-F]{2,4}/g, (m) => String.fromCharCode(parseInt(m.slice(2), 16)));  

  function decodeHtmlString(encodedString) {

    try{
      if (encodedString.length > 0){
        return decodeURIComponent(encodedString.replace(/\+/g, " "));
      } else {
        return ''
      }  
    } catch (error) {
      if (error instanceof URIError){
        myDebug(4, `DMDashboard-decodeHtmlString: Malformed URI Component ${error}`)
        return encodedString;
      } else {
        throw error;
      }
    }
  }
  function showCharImage(toId, imageIndex, bTitle, bWhisper) {
    // imageIndex: -1 all images, 0+: image index
    // bTitle, 0: No Title, 1: Title(In Template Box) 2: Just Image
    let msgPrefix = '';
    let msgSendToPlayers = '';
    let msg = '';
    let artwork = '';
    let bio = '';
    let img = '';
    let bArtworkFound = false;
  
    if (bWhisper == 0){bWhisper = false} else {bWhisper = true}

    myDebug(3, `showCharImage: Tid:${toId}, Img: ${imageIndex},  Title:${bTitle}, Whisper:${bWhisper}`)

    let tObj = getObj('graphic', toId);
    if (tObj) {
      let cObj = getObj('character', tObj.get('represents'));
      if (cObj){

        let name = '';
        if (bTitle==1){
          name = cObj.get('name');  
        }

        cObj.get('bio', function(bio){
          myDebug(3, `showCharImage: BIO: ${bio}`)
          if (bio != null && bio != undefined){
            bio = decodeUnicode(bio);

            myDebug(3, `showCharImage: Img: ${imageIndex}`)
            if (imageIndex == -1) { // All Images
              myDebug(3, `showCharImage: ALL`)
              artwork = bio.match(/\<img src.*?\>/g)
            } else { // A specific Index
              myDebug(3, `showCharImage: One(${imageIndex}`)
              artwork = bio.match(/\<img src.*?\>/g);
              artwork = String(artwork);
              if (imageIndex > (artwork.split(",")).length) {
                imageIndex = 0
              }
              artwork = artwork.split(",")[imageIndex]
              myDebug(3, `showCharImage: Specific Img: ${artwork}`)
            }

            myDebug(3, `showCharImage: Artwork: ${artwork}`)
            if ((''+artwork).length > 10) {
              msg = artwork;
              bArtworkFound = true;
            } else {
              msg = 'No artwork exists for this character.';
              myDebug(3, `showCharImage: No Artwork: ${artwork}`)
            }

          } else {
            msg = 'No Bio exists for this character'
          }

          let msgPrefix = '';
          if (bWhisper){
            msgPrefix='/w gm '
            msgSendToPlayers = `\nSend Image to Players:`
            artwork = String(artwork)
            let imgCnt = (artwork.split(",").length);
            for (ndx = 0;  ndx < imgCnt; ndx++) {
              msgSendToPlayers += `\nImage ${ndx+1} [w/ Title](!DMDash --SHOWIMAGE ${toId} ${ndx} 1 0) [w/o Title](!DMDash --SHOWIMAGE ${toId} ${ndx} 0 0)`
            }
          }

          if (!bArtworkFound){
            msgSendToPlayers = '' ;
          }

          myDebug(3, `ShowImage: ArtWork ${artwork}`)
          myDebug(3, `ShowImage: name ${name}`)

          msg = `${msgPrefix}&{template:npcaction}{{rname=${name}}} {{description=${msg} ${msgSendToPlayers}}}`;

          myDebug(3, msg);

          sendChat(name, msg);

        });
      }
    }
  }
  function getTokenType(toId) {
    // Given a TurnOrderId 
    //  Returns UTILITY, CUSTOM, PC, NPC, or OTHER

    let cObj = []; //Character
    let tObj = []; //Token

    if (toId == -1) {
      return 'CUSTOM';
    }

    tObj = getObj("graphic", toId);
    if (!tObj) {
      return 'OTHER';
    }

    if(tObj.get("represents") == ""){
      return 'OTHER';
    }

    cObj = getObj("character", tObj.get('represents'));
    if (!cObj){
      return 'OTHER';
    }

    // Ignore Tokens tied to DM Functionality
    if (myGetAttrByName(cObj.get('_id'),'Init_Ignore','current')==1){
      return 'UTILITY';
    }

    if (cObj.get('controlledby') == '' && getAttrByName(cObj.get('_id'),'npc','current')==1){
      return 'NPC';
    };

    if (cObj.get('controlledby') == '' && getAttrByName(cObj.get('_id'),'npc','current')==0){
      return 'NPC-CHARSHEET';
    };

    // If we got all here - then this Turnorder Id is associated with a player controlled 
    return 'CHAR';
  };
  function getGMPlayerID() {
    const gmPlayers = findObjs({ _type: 'player' }).filter((player) => {
      return playerIsGM(player.get('_id'));
    });
    if (gmPlayers.length > 0) {
      return gmPlayers[0].get('_id');
    } else {
      log('No GM found');
      return null;
    }
  }
  function getAllPlayerIDsArray() {
    const players = findObjs({ _type: 'player' });
    const playerIds = players.map((player) => player.get('_id'));

    return playerIds;
  }
  function pingToken(tokenId, mode) {
    //when Mode is:
    //  mode=0; Private ping for GM Only
    //  mode=1; Public ping for all
    const gmId = getGMPlayerID();
    const token = getObj('graphic', tokenId);

    if (token) {
      const left = token.get('left');
      const top = token.get('top');
      const pageId = token.get('_pageid');

      // Private or Public ping?
      if (mode=0){
        // Private
        sendPing(left, top, pageId, gmId, true, gmId);
      } else {
        // Public
        const playerIds = getAllPlayerIDsArray();
        sendPing(left, top, pageId, gmId, true, playerIds);
      }
    } else {
      //log('Token not found');
    }
  }
  function to_MoveNext() {
    // First - Make sure the turnorder is populated
    var turnOrder = Campaign().get('turnorder');
    if (turnOrder === '') {
        sendChat('AdvanceTurnOrder', 'Turn order is empty.');
        return;
    }
    // take the JSON string and convert it to an object
    turnOrder = JSON.parse(turnOrder);
    if (turnOrder.length === 0) {
      sendChat('AdvanceTurnOrder', 'Turn order is empty.');
      return;
    }

    // Move the current item to the end of the list
    var currentItem = turnOrder.shift();
    turnOrder.push(currentItem);

    // Update any countdown formulas
    if (turnOrder[0].id == -1 && turnOrder[0].formula && turnOrder[0].formula !== '') {
            var newValue = turnOrder[0].pr + eval(turnOrder[0].formula);
            turnOrder[0].pr = newValue;
    }

    // Update the campaign's turn order
    turnOrder = JSON.stringify(turnOrder);
    Campaign().set('turnorder', turnOrder);
  }
  function to_MovePrev() {
    // First - Make sure the turnorder is populated
    var turnOrder = Campaign().get('turnorder');
    if (turnOrder === '') {
        sendChat('PrevTurnOrder', 'Turn order is empty.');
        return;
    }

    // take the JSON string and convert it to an object
    turnOrder = JSON.parse(turnOrder);
    if (turnOrder.length === 0) {
      sendChat('PrevTurnOrder', 'Turn order is empty.');
      return;
    }
    last_turn = turnOrder.pop();        
    turnOrder.unshift(last_turn);
    Campaign().set('turnorder', JSON.stringify(turnOrder));
  }
  function to_AddCustom(itemName, position, formula) {
    const turnorder = JSON.parse(Campaign().get('turnorder') || '[]');
    let newItem = [];
    if (formula){
      newItem = {
        id: "-1",
        pr: position, // Set the desired initiative value (default: 0)
        formula: formula, 
        custom: itemName,
        _pageid: Campaign().get("playerpageid")
      };
    } else {
      newItem = {
        id: "-1",
        pr: position, // Set the desired initiative value (default: 0)
        custom: itemName,
        _pageid: Campaign().get("playerpageid")
      };    
    }

    turnorder.unshift(newItem);
    Campaign().set('turnorder', JSON.stringify(turnorder));
  }
  function to_Sort() {
    let turnOrder = JSON.parse(Campaign().get('turnorder'));
    if (!turnOrder || turnOrder.length === 0) {
        sendChat('SortTurnOrder', '/w gm Turn order is empty, unable to sort.');
        return;
    }
    turnOrder.sort(function(a, b) {
        if (a.pr < b.pr) return 1;
        if (a.pr > b.pr) return -1;
        return 0;
    });

    Campaign().set('turnorder', JSON.stringify(turnOrder));
    // sendChat('SortTurnOrder', '/w gm Turn order has been sorted by initiative.');
  }
  function to_SortWrapped() {
    let turnOrder = JSON.parse(Campaign().get('turnorder'));

    if (!turnOrder || turnOrder.length === 0) {
        sendChat('SortTurnOrder', '/w gm Turn order is empty, unable to sort.');
        return;
    }
    let anchorItem = turnOrder[0];

    // Sort the turnorder proper
    turnOrder.sort(function(a, b) {
        if (a.pr < b.pr) return 1;
        if (a.pr > b.pr) return -1;
        return 0;
    });

    // Rotate the turn order until it matches our Anchor item
    let x = 0; // safeguard to prevent an infinite loop
    while(anchorItem.pr != turnOrder[0].pr && anchorItem.id != turnOrder[0].id && x <= turnOrder.length){
      // Rotate Turn Order
      var currentItem = turnOrder.shift();
      turnOrder.push(currentItem);
      x = x + 1;
    }
    Campaign().set('turnorder', JSON.stringify(turnOrder));
  }
  function to_Clear() {
    Campaign().set('turnorder', '[]');
  }  
  function to_Remove(itemId){
    const turnOrder = JSON.parse(Campaign().get('turnorder'));
    if (!turnOrder || turnOrder.length === 0) {
      // log(`to_Remove: Turn order is empty, unable to remove item.`);
      return;
    }
    const itemToRemove = turnOrder.findIndex(item => item.id === itemId);
    if (itemToRemove === -1) {
      // log(`to_Remove: Item with ID "${itemId}" not found in the turn order.`);
      return;
    }
    turnOrder.splice(itemToRemove, 1);
    Campaign().set('turnorder', JSON.stringify(turnOrder));
    // log(`to_Remove: Item with ID "${itemId}" has been removed from the turn order.`);
  }
  function to_RemoveCustom(ndx){
    const turnOrder = JSON.parse(Campaign().get('turnorder'));
    if (!turnOrder || turnOrder.length === 0) {
      // log(`to_RemoveCustom: Turn order is empty, unable to remove item.`);
      return;
    }
    if (ndx < 0 || ndx > (turnOrder.length-1)) {
      // log(`to_RemoveCustom: Item with Index Position of "${ndx}" is outside the range of turn order.`);
      return;
    }
    turnOrder.splice(ndx, 1);
    Campaign().set('turnorder', JSON.stringify(turnOrder));
    // log(`to_RemoveCustom: Item with Index Position of "${ndx}" has been removed from the turn order.`);
  }
  const getRepeatingSectionAttrs = function (charid, prefix) {
    // #### Not Used - Could be moved to my Library of functions ###
    // Input
    //  charid: character id
    //  prefix: repeating section name, e.g. 'repeating_weapons'
    // Output
    //  repRowIds: array containing all repeating section IDs for the given prefix, ordered in the same way that the rows appear on the sheet
    //  repeatingAttrs: object containing all repeating attributes that exist for this section, indexed by their name
    const repeatingAttrs = {},
        regExp = new RegExp(`^${prefix}_(-[-A-Za-z0-9]+?|\\d+)_`);
    let repOrder;
    // Get attributes
    findObjs({
        _type: 'attribute',
        _characterid: charid
    }).forEach(o => {
      const attrName = o.get('name');
      // log('getRepeatingSectionAttrs 1: ' + attrName);
      if (attrName.search(regExp) === 0) repeatingAttrs[attrName] = o;
      else if (attrName === `_reporder_${prefix}`) repOrder = o.get('current').split(',');
    });
    if (!repOrder) repOrder = [];
    // Get list of repeating row ids by prefix from repeatingAttrs
    const unorderedIds = [...new Set(Object.keys(repeatingAttrs)
      .map(n => n.match(regExp))
      .filter(x => !!x)
      .map(a => a[1]))];
    const repRowIds = [...new Set(repOrder.filter(x => unorderedIds.includes(x)).concat(unorderedIds))];
    return [repRowIds, repeatingAttrs];
  }
  function makeMenuButton(name, link, selected, minwidth) {
    // let buttonStyle = `background-color: red; color:yellow !important; font-weight:normal; border-radius: 1px; padding: 1px; margin: 1px 1px 1px 0px; display: inline-block`;  
    // let buttonStyle = `display: flex; flex-direction: column; align-items: center; padding: 6px 14px; font-family: -apple-system, BlinkMacSystemFont, 'Roboto', sans-serif; border-radius: 6px; border: none; background: #6E6D70; box-shadow: 0px 0.5px 1px rgba(0, 0, 0, 0.1), inset 0px 0.5px 0.5px rgba(255, 255, 255, 0.5), 0px 0px 0px 0.5px rgba(0, 0, 0, 0.12); color: #DFDEDF; user-select: none; -webkit-user-select: none; touch-action: manipulation;`
    let buttonStyle = `background-color: #521E10; border: 1px; color: white; text-align: center; display: inline-block; font-size: 14px; margin: 2px 1px; cursor: pointer; padding: 3px 6px; border-radius: 4px;`

    if (selected == true || selected == 1){
      myDebug(3, `Button ${name} selected`);
      buttonStyle = `background-color: #FFAD00; border: 1px; color: black; text-align: center; display: inline-block; font-size: 14px; margin: 2px 1px; cursor: pointer; padding: 3px 6px; border-radius: 4px;`
    }

    if (minwidth === "f") {
      minwidth = "100%"
    } else {
      if (!minwidth) {
        minwidth = 'NONE'
      }
      minwidth = minwidth + "px"
    }

    if (minwidth == 'NONE'){
      minwidth = '';
    } else {
      minwidth =`width:${minwidth}`
    }

    if (!name) {
      name = "untitled"
    }

    if (link) {
      return `<a style = '${buttonStyle}; ${minwidth} !important' href='${link}'>${name}</a>`;
    } else {
      return `<div style = '${buttonStyle}; ${minwidth}; display:inline-block !important'>${name}</div>`;
    }
  }
  function makeButton(name, link, minwidth) {
    //const buttonStyle = `background-color: #cccccc25; color: #521E10 !important; font-weight:bold; border-radius: 0px; padding: 0px; margin: 1px 1px 1px 0px; display: inline-block`;  
    const buttonStyle = `background-color: #00000000; color: #521E10 !important; font-weight:bold; border-radius: 0px; padding: 0px; margin: 1px 1px 1px 0px; display: inline-block`;      
    if (minwidth === "f") {
      minwidth = "100%"
    } else {
      if (!minwidth) {
        minwidth = 'NONE'
      }
      minwidth = minwidth + "px"
    }

    if (minwidth == 'NONE'){
      minwidth = '';
    } else {
      minwidth =`width:${minwidth}`
    }

    if (!name) {
      name = "untitled"
    }

    if (link) {
      return `<a style = '${buttonStyle}; ${minwidth} !important' href='${link}'>${name}</a>`;
    } else {
      return `<div style = '${buttonStyle}; ${minwidth}; display:inline-block !important'>${name}</div>`;
    }
  }
  function makeHelpButton(title, helpText) { // #### Not Used - Could be moved to my Library of functions ###
    return `<div style = 'float:right '><a style = 'color: red; display:inline-block; padding:0px; margin:0px; background-color:white; border-radius:8px;' href = '!survey --sendtext|${title}|${helpText}'>&nbsp;?&nbsp;</a></div>`
  }
  function makeBox(color, id, name) { // #### Not Used - Could be moved to my Library of functions ###
    return `<a href = '!survey --pcs ${id}' style= 'float: left; display:inline-block; height: 20px; width: 20px;  margin-top: 2px; margin-right: 2px; background-color:${color}; border: 1px solid black; clear: both; !important'</a>`;
  }
  function formatAsPercent(num) { // #### Not Used - Could be moved to my Library of functions ###
    return new Intl.NumberFormat('default', {
      style: 'percent',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
    }).format(num / 100);
  }
  function resolveAttr(cid, attname) {

    // log('debug ra 1' + attname);

    let attobj = findObjs({
      type: 'attribute',
      characterid: cid,
      name: attname
    }, { caseInsensitive: true })[0];

    if (!attobj) {
      return { name: '', current: '', max: '' };
    }
    let att2 = { name: attobj.get('name'), current: attobj.get('current'), max: attobj.get('max') };
    return att2;
  }
  function getCharMainAtt2(cid2) { // #### Not Used - Could be moved to my Library of functions ###
    let tbl = '<table border=0><tr>';
    let z = '';
    let wantedAttributes = ['strength', 'dexterity', 'constitution', 'intelligence', 'wisdom', 'charisma'];
    wantedAttributes.forEach(myAtt => {
      let ca = resolveAttr(cid2, myAtt);
      let caName = ca.name.slice(0, 1).toUpperCase();
      let caVal = ca.current;
      let caMod = resolveAttr(cid2, myAtt + '_mod');
      let caModVal = caMod.current;
      if (caModVal >0){caModVal='+' + caModVal}
      tbl += '<td><strong>' + caName + ':</strong>&nbsp;' + caModVal + '(' + caVal + ')</td>';
      z += caName + '(' + caModVal +')';
    });
    tbl += '</tr></table>';
    return z;
  }
  function changeAttributeValue(characterId, attributeName, newValue, maxValue) {
    // Get the attribute object
    let attribute = findObjs({
      _type: 'attribute',
      characterid: characterId,
      name: attributeName,
    })[0];

    // Check if the attribute exists
    if (attribute) {
      // Update the attribute with the new value
      attribute.set('current', newValue);
    } else {
      // Create the attribute with the specified name and value
      // log(`Create Attribute ${attributeName} for ${characterId} value ${newValue} Max ${maxValue}`)      
      if (!maxValue){
        createObj('attribute', {
          characterid: characterId,
          name: attributeName,
          current: newValue
        });
      } else {
      createObj('attribute', {
        characterid: characterId,
        name: attributeName,
        current: newValue,
        max: maxValue
        });
      }
    }
  }
  function getCharacterByName(characterName) { // #### Not Used - Could be moved to my Library of functions ###
  // Find the character with the specified name
  let character = findObjs({
    _type: 'character',
    name: characterName,
  })[0];

  // Return the character object or null if not found
  return character || null;
  }
  function getSMImages(sm) {
    // Build an array of Token Status Marker images for later use
    let x = 0;
    let y = 0;
    let smList = sm.split(','); // Split the input string into an array of tags
    let sm_url = '';
    let sm_Images = '';

    // Moved to the top of HandleMsg to improve performance - no need to load campaign markers every time
    //const tokenMarkers = JSON.parse(Campaign().get("token_markers"));

    // #### This may be sped up, although, it only becomes a burden if there are a ton of status markers on tokens ###
    // Loop through the list of tags
    for (x = 0; x < smList.length; x++) {
      // Loop through the tokenMarkers array
      for (y = 0; y < tokenMarkers.length; y++) {
        // Check if the tag matches an element in the tokenMarkers array
        if (tokenMarkers[y].tag === smList[x]) {
          sm_url = tokenMarkers[y].url; // Get the URL associated with the tag
          break;
        }
      }
      // Concatenate the img element with the corresponding URL
      sm_Images += `<div title="${smList[x]}"> <img style='max-height: 20px; max-width: 20px; padding: 0px; margin: 0px !important' src='${sm_url}'></img></div>`;
    }
    return sm_Images; // Return the final string containing the img elements
  }
  function AddSign(v){
    if (v>0){
      v='+' + v;
    }
    return v;
  }

  const getAttrCountByChar = () => findObjs({ // #### Not Used - Could be moved to my Library of functions ###
    type: 'attribute'
    })
    .map(o => o.get('characterid'))
    .reduce((m, o) => ({
        ...m,
        [o]: (m[o] || 0) + 1
    }), {});
  const getAbilityCountByChar = () => findObjs({ // #### Not Used - Could be moved to my Library of functions ###
    type: 'ability'
    })
    .map(o => o.get('characterid'))
    .reduce((m, o) => ({
        ...m,
        [o]: (m[o] || 0) + 1
    }), {}
  );
  function getSystemTimeInSecs()
  {
    let d = new Date();
    s = d.getUTCDate() * 3600 * 24;
    s += d.getUTCHours() * 3600;
    s += d.getUTCMinutes() * 60;
    s += d.getUTCSeconds();
    // log('Date Time: ' + d.getUTCDate() + ' '  + d.getUTCHours() + ':' + d.getUTCMinutes() + ':' + d.getUTCSeconds())
    return s;
  }
  function resetAttributeValue(attr, def) {
    findObjs({ _type: 'attribute', name: attr }).forEach((attr) => {
      attr.set('current', def);
    });
  }
  function ResetStats(){

    //Reset Player Stats
    // Look for every Attribute named to_secs, to_count or to_avg and set their current value equal to their max value
    resetAttributeValue('to_secs', 0);
    resetAttributeValue('to_count', 0);
    resetAttributeValue('to_avg', 0);

    //Reset DM/State Stats
    state.DMDashboard.DM_Secs = 0;
    state.DMDashboard.DM_Count = 0;
    state.DMDashboard.DM_Avg = 0;
  }
  function randomInteger(min, max) {
    const randomDecimal = Math.random();

    // Scale the random decimal number to the desired range [min, max]
    const scaledRandom = randomDecimal * (max - min + 1) + min;

    // Truncate the decimal part and return the resulting integer
    return Math.floor(scaledRandom);
  }
  function setHPBar(bar){
    if (bar < 1 || bar > 3) {
      sendChat('DM Dashboard', `/w gm Error setting HP Bar value to <b>${bar}</b>!`)
    }
    state.DMDashboard.HPBar = bar
    sendChat('DM Dashboard', `/w gm ${openChat}DM Dashboard is now using Bar <b>${bar}</b> for HP.${closeChat}`);                    
  }
  function mySendChat(whisper, title, message, links){
    let chatTxt = '';

    if(whisper == true){
      chatTxt = `/w gm `
    }
    chatTxt += `${openChat}<table style="${tblChatStyle}"><tr style="${trhChatStyle}"><td style="${tdChatStyle}">${title}</td></tr>`
    chatTxt += `<tr><td style="${tdChatStyle}">${message}</td></tr>`
    if (links){
      chatTxt += `<tr><td style="${tdChatStyle}">${links}</td></tr>`
    }
    chatTxt += `</table>${closeChat}`
    sendChat('DMDashboard', chatTxt);
  }
  function setSBHeights(charBoxHeight, toBoxHeight){
    if (charBoxHeight == 0 || toBoxHeight == 0) {
      charBoxHeight = '200px'
      toBoxHeight = '40vh'
    }
    state.DMDashboard.charBox_ScrollHeight = charBoxHeight;
    state.DMDashboard.toBox_ScrollHeight = toBoxHeight;
  }
  function addTooltip(tt, item) {
    let newItem = `<div style="display:inline;" title="${tt}">${item}</div>`
    return newItem;
  }
  function replaceDynamicSpanElement(source, spanId, item){
    const regexPattern = `(<span id=${spanId}>)(.*?)(</span>)`;
    const regexFlags = 'gi';
    const regex = new RegExp(regexPattern, regexFlags);
    const newSource = source.replace(regex, `$1${item}$3`);
    //log(`replaceDynamicSpanElement: Pattern: ${regexPattern} regex:${regex} spanId:${spanId} item:${item}`);
    //log(`replaceDynamicSpanElement: source:${source}`);
    return newSource;
  }
  function CR_to_CharLvl(cr, bSpellcaster){
    // Basedon on some math I found online
    //  Spellcaster level x 2/3 = CR
    //  Non-spellcaster level x 1/2 = CR
    let lvl = 0;

    if (cr == '1/8'){cr = Number(0.125)}
    if (cr == '1/4'){cr = Number(0.25)}
    if (cr == '1/2'){cr = Number(0.5)}

    if (bSpellcaster) {
      lvl = Math.round(Number(cr) * Number(3) / Number(2));
    } else {
      lvl = Math.round(Number(cr) * Number(2));
    }
    return lvl;
  }
  function CharLvl_to_CR(lvl, bSpellcaster){
    // Basedon on some math I found online
    //  Spellcaster level x 2/3 = CR
    //  Non-spellcaster level x 1/2 = CR
    let cr = 0;
    if (bSpellcaster) {
      cr = Number(lvl) * (Number(2) / Number(3));
    } else {
      cr = Number(lvl) * Number(0.5);
    }
    return cr;
  }
  function CR_to_XP(cr){
    const tblCR2XP =  [[0,0],[0.125,25],[0.25,50],[0.5,100],[1,200],[2,450],[3,700],[4,1100],[5,1800],[6,2300],[7,2900],[8,3900],[9,5000],[10,5900],[11,7200],[12,8400],[13,10000],[14,11500],[15,13000],[16,15000],[17,18000],[18,20000],[19,22000],[20,25000],[21,33000],[22,41000],[23,50000],[24,62000],[25,75000],[26,90000],[27,105000],[28,120000],[29,135000],[30,155000]];
    if (cr == '1/8'){cr = Number(0.125)}
    if (cr == '1/4'){cr = Number(0.25)}
    if (cr == '1/2'){cr = Number(0.5)}

    let xp = Math.round(interpolate(cr, tblCR2XP));
    return xp;
  }
  function addInitiative(tokenIds) {
    //tokenIds will be a comma-delimited list of tokenids
    const aryTokenIds = tokenIds.split(',');

    let turnOrder = JSON.parse(Campaign().get("turnorder"));
    if (!turnOrder){
      turnOrder = [];
    }

    aryTokenIds.forEach(tId => {

      let tType = getTokenType(tId);

      let tObj = getObj("graphic", tId);
      if (!tObj){ 
        return; 
      }

      let cObj = getObj("character", tObj.get('represents'));
      if (!cObj){ 
        return; 
      }

      let d20Roll = randomInteger(1,20);
      let initBonus = getAttrByName(cObj.get('_id'),'initiative_bonus','current');
      initBonus = parseFloat(Number(initBonus.toFixed(2))); // Fix Roll20 issue where init bonus has a lot of significant digits.
      let result = Number(d20Roll) + Number(initBonus)
      result = Math.round(result * 100)/100

      let chatMsg = '';      
      if (tType == 'CHAR') {
        chatMsg = `@{${cObj.get('name')}|wtype}&{template:simple} {{rname=^{init-u}}} {{r1=[[${d20Roll}[INIT]+[[${initBonus}]][DEX]]]}} {{normal=1}} @{${cObj.get('name')}|charname_output}`
      } else if (tType == 'NPC') {
        chatMsg = `/w gm @{${cObj.get('name')}|wtype}&{template:npc} @{${cObj.get('name')}|npc_name_flag} {{rname=^{init}}} {{r1=[[${d20Roll}+[[${initBonus}]][DEX]]]}} {{normal=1}} {{type=Initiative}}`
      } else if (tType == 'NPC-CHARSHEET') {
        chatMsg = `w/ gm @{${cObj.get('name')}|wtype}&{template:simple} {{rname=^{init-u}}} {{r1=[[${d20Roll}[INIT]+[[${initBonus}]][DEX]]]}} {{normal=1}} @{${cObj.get('name')}|charname_output}`
      } else {
        return;
      }

      sendChat('API', chatMsg);
      // log(`AddInitiative: tId:${tId} result: ${result} TO-Length:${turnOrder.length}`)
      // Add to the TurnOrder
      let newTOItem = {
        id: tId,
        pr: result,
        custom: '',
        _pageid: Campaign().get("playerpageid"),        
      };

      turnOrder.push(newTOItem);

      to_Remove(tId); // Remove the turnorder item if it already exists.  

      // log(`AddInitiative: PageId:${Campaign().get("playerpageid")} TO-Length:${turnOrder.length}`)

    });
    Campaign().set("turnorder", JSON.stringify(turnOrder));
    
  }
  function buildTraitOutput(charName, traitName, traitSource, traitDescription) {
    // Roll Template: @{Thorin|wtype}&{template:traits} @{Thorin|charname_output} {{name=Fighting Style: Interception}} {{source=Class: Paladin}} {{description=When a creature you...}}
    //let traitOutput = `!${htmlCR}${htmlAt}{${charName}|wtype${htmlRBrace}`

    // the Question Mark (?) is the key to getting the browser to accept this link
    let traitOutput = `!${htmlCR}/w GM ?@{${charName}|wtype${htmlRBrace}`
    traitOutput += `&amp;{template:traits${htmlRBrace}`
    traitOutput += `@{${charName}|charname_output${htmlRBrace}`
    traitOutput += `{{name=${traitName}${htmlRBrace}${htmlRBrace}` 
    if (traitSource && traitSource.length>0) {  //NPCs generally don't have sources
      traitOutput += `{{source=${traitSource}${htmlRBrace}${htmlRBrace}`
    }
    traitDescription = HE(traitDescription)
    traitOutput += `{{description=${traitDescription}${htmlRBrace}${htmlRBrace}`;
    myDebug(2, traitOutput)
    return traitOutput;
  }

  function buildRollOutput(){
    // let rollOutput = `!${htmlCR}?${htmlAt}${htmlRBrace}${toChar.get('name')}|wtype${htmlRBrace}&amp;${htmlLBrace}template:npc${htmlRBrace}&nbsp;${htmlAt}{${toChar.get('name')}|npc_name_flag}&nbsp;${htmlAt}${htmlLBrace}${toChar.get('name')}|rtype${htmlRBrace}+[[@{${toChar.get('name')}|npc_${mySkill}}]]]]&#125;&#125; {{r1=[[@{${toChar.get('name')}|d20}+[[@{${toChar.get('name')}|npc_${mySkill}}]]]]&#125;&#125; {{mod=[[[[@{${toChar.get('name')}|npc_${mySkill}}]]]]&#125;&#125; {{rname=${mySkill}&#125;&#125; {{type=Skill&#125;    let myRoll = `!&#13;?&#64;{${toChar.get('name')}|wtype}&amp;&#123;template:npc&#125;&nbsp;&#64;{${toChar.get('name')}|npc_name_flag}&nbsp;&#64;{${toChar.get('name')}|rtype&#125;+[[@{${toChar.get('name')}|npc_${mySkill}}]]]]${htmlRBrace}${htmlRBrace} ${htmlLBrace}${htmlLBrace}r1=[[${htmlAt}${htmlLBrace}${toChar.get('name')}|d20${htmlRBrace}+[[${htmlRAt}${htmlLBrace}${toChar.get('name')}|npc_${mySkill}${htmlRBrace}]]]]${htmlRBrace}${htmlRBrace} ${htmlLBrace}${htmlLBrace}mod=[[[[${htmlAt}${htmlLBrace}${toChar.get('name')}|npc_${mySkill}${htmlRBrace}]]]]${htmlRBrace}${htmlRBrace} ${htmlRbrace}${htmlLBrace}${htmlLBrace}rname=${mySkill}${htmlRBrace}${htmlRBrace} ${htmlLBrace}${htmlLBrace}type=Skill${htmlRBrace}${htmlRBrace}

    return rollOutput;
  }

  function LoadEncounterRating() {
    // As described in: https://www.dndbeyond.com/sources/dmg/creating-adventures#CreatingaCombatEncounter

    let mapXPThresholdsByCharLevel = new Map();
    mapXPThresholdsByCharLevel.set(0, {level: 0, easy:0, medium:0, hard:0, deadly:0})
    mapXPThresholdsByCharLevel.set(1, {level: 1, easy:25, medium:50, hard:75, deadly:100})
    mapXPThresholdsByCharLevel.set(2, {level: 2, easy:50, medium:100, hard:150, deadly:200})
    mapXPThresholdsByCharLevel.set(3, {level: 3, easy:75, medium:150, hard:225, deadly:400})
    mapXPThresholdsByCharLevel.set(4, {level: 4, easy:125, medium:250, hard:375, deadly:500})
    mapXPThresholdsByCharLevel.set(5, {level: 5, easy:250, medium:500, hard:750, deadly:1100})
    mapXPThresholdsByCharLevel.set(6, {level: 6, easy:300, medium:600, hard:900, deadly:1400})
    mapXPThresholdsByCharLevel.set(7, {level: 7, easy:350, medium:750, hard:1100, deadly:1700})
    mapXPThresholdsByCharLevel.set(8, {level: 8, easy:450, medium:900, hard:1400, deadly:2100})
    mapXPThresholdsByCharLevel.set(9, {level: 9, easy:550, medium:1100, hard:1600, deadly:2400})
    mapXPThresholdsByCharLevel.set(10, {level: 10, easy:600, medium:1200, hard:1900, deadly:2800})
    mapXPThresholdsByCharLevel.set(11, {level: 11, easy:800, medium:1600, hard:2400, deadly:3600})
    mapXPThresholdsByCharLevel.set(12, {level: 12, easy:1000, medium:2000, hard:3000, deadly:4500})
    mapXPThresholdsByCharLevel.set(13, {level: 13, easy:1100, medium:2200, hard:3400, deadly:5100})
    mapXPThresholdsByCharLevel.set(14, {level: 14, easy:1250, medium:2500, hard:3800, deadly:5700})
    mapXPThresholdsByCharLevel.set(15, {level: 15, easy:1400, medium:2800, hard:4300, deadly:6400})
    mapXPThresholdsByCharLevel.set(16, {level: 16, easy:1600, medium:3200, hard:4800, deadly:7200})
    mapXPThresholdsByCharLevel.set(17, {level: 17, easy:2000, medium:3900, hard:5900, deadly:8800})
    mapXPThresholdsByCharLevel.set(18, {level: 18, easy:2100, medium:4200, hard:6300, deadly:9500})
    mapXPThresholdsByCharLevel.set(19, {level: 19, easy:2400, medium:4500, hard:7300, deadly:10900})
    mapXPThresholdsByCharLevel.set(20, {level: 20, easy:2800, medium:5700, hard:8500, deadly:12700})
    return mapXPThresholdsByCharLevel;
  
  }

  function makeDBPlayButton(name){
    let btn = ''
    let dbItem = [];
    
    dbItem = state.DMDashboard.JB_DashboardBtns.find(dbTrack => dbTrack.item === name);
    if (dbItem) {
      btn = addTooltip('Play sound', makeButton(name, `!DMDash --playtrack ${dbItem.id}`));
    } else {
      btn = addTooltip('Track not set!',name)
    }
    return btn;
  }

  // ****************************
  // *** Refresh Reports Function ***  
  // ****************************
  function refreshReports() {
    // **** Variable Declarations  ****
    let lines = '';
    let rows = [];
    let urlCount = 0;
    let dmChar = [];
    let toToken = [];
    let toChar = [];
    let charBtn = '';
    let tknImg = '';
    let skills = '';
    let repeatingSection = '';
    let repeatingName = '';
    let repeatingValues = [];
    let repeatingValue = 0;
    let repeatingMax = 0;
    let hp = 0;
    let hpmax = 0;
    let hppct = 0;
    let btnAdjHP = '';
    let tmp = '';

    let row =[];
    let repeatingField='';
    let spell_lvls = ['cantrip', '1', '2', '3', '4', '5', '6', '7', '8', '9'];
    let slots = '';
    let slots_remaining = '';

    let i=0;
    let x=0;
    let y=0;
    let tType = '';
    let charSorted = [];
    let characterList = '';
    let charReport = '';
    let toList='';
    let toObj=[];
    let tokenList = '';
    let pcList = '';
    let isnpc = 0;
    let sm_Images='';
    let tt = '';
    let sm = '';
    let cb = '';
    let npc = '';
    let speed = '';
    let pp = '';
    let dt_diff = 0;
    let TO_Avg = 0;
    let TO_Secs = 0;
    let TO_Count = 0;

    // Table Formating Variables  
    let ts = ' style="border: 0px; padding: 0;border-collapse:collapse;border-color:#93a1a1;border-spacing:0" class="tg"; margin: 0 auto;';
    let th = ' style="background-color:#657b83;border-color:#93a1a1;border-style:solid;border-width:0px;color:#fdf6e3;font-family:Arial, sans-serif;font-size:12px;font-weight:normal;overflow:hidden;padding:1px 1px;text-align:left;vertical-align:top;word-break:normal;"';
    let tdbak = ' style="background-color:#DCDACF;border-color:#93a1a1;border-style:solid;border-width:1px;color:#002b36;font-family:Arial, sans-serif;font-size:12px;overflow:hidden;padding:1px 1px;text-align:left;vertical-align:top;word-break:normal"';
    let td = ' style="background-color:#EFEBD6;border-color:#EFEBD6;border-style:solid;border-width:0px;color:#521E10;font-family:ScalySansRemake, sans-serif;font-size:12px;overflow:hidden;padding:1px 1px;text-align:left;vertical-align:top;word-break:normal"';
    let td1bak = ' style="background-color:#F9EBEA;border-color:#93a1a1;border-style:solid;border-width:1px;color:#002b36;font-family:Arial, sans-serif;font-size:12px;overflow:hidden;padding:1px 1px;text-align:left;vertical-align:top;word-break:normal"';
    let td1 = ' style="background-color:#E1E4C4;border-bottom: 1px;border-color:#E1E4D4;border-style:solid;border-width:0px;color:#521E10;font-family:Arial, sans-serif;font-size:12px;overflow:hidden;padding:1px 1px;text-align:left;vertical-align:top;word-break:normal"';    
    let tdcustom = ' style="background-color:#BEAD70;border-color:#93a1a1;border-style:solid;border-width:0px;color:#521E10;font-family:Arial, sans-serif;font-size:12px;overflow:hidden;padding:1px 1px;text-align:left;vertical-align:top;word-break:normal"';        
    let td2 = ' style="background-color:#BBDEFB;border-color:#93a1a1;border-style:solid;border-width:0px;color:#002b36;font-family:Arial, sans-serif;font-size:12px;overflow:hidden;padding:1px 1px;text-align:left;vertical-align:top;word-break:normal"';
    let td3 = ' style="background-color:#BBCAFA;border-color:#93a1a1;border-style:solid;border-width:0px;color:#002b36;font-family:Arial, sans-serif;font-size:12px;overflow:hidden;padding:1px 1px;text-align:left;vertical-align:top;word-break:normal"';
    let td4 = ' style="background-color:#DAF7A6;border-color:#93a1a1;border-style:solid;border-width:0px;color:#002b36;font-family:Arial, sans-serif;font-size:12px;font-weight:bold;overflow:hidden;padding:1px 1px;text-align:left;vertical-align:top;word-break:normal"';
    let tdpc = td;
    let tdnpc = td1;

    let tdbase = tdpc;
    let td_health100 = ' style="background-color:#24CE10;border-color:#93a1a1;border-style:solid;border-width:1px;color:#002b36;font-family:Arial, sans-serif;font-size:12px;font-weight:bold;overflow:hidden;padding:1px 1px;text-align:center;vertical-align:top;word-break:normal"';
    let td_health75 = ' style="background-color:#FFFE00;border-color:#93a1a1;border-style:solid;border-width:1px;color:#002b36;font-family:Arial, sans-serif;font-size:12px;font-weight:bold;overflow:hidden;padding:1px 1px;text-align:center;vertical-align:top;word-break:normal"';
    let td_health50 = ' style="background-color:#FFAC00;border-color:#93a1a1;border-style:solid;border-width:1px;color:#002b36;font-family:Arial, sans-serif;font-size:12px;font-weight:bold;overflow:hidden;padding:1px 1px;text-align:center;vertical-align:top;word-break:normal"';
    let td_health25 = ' style="background-color:#FF5A00;border-color:#93a1a1;border-style:solid;border-width:1px;color:#002b36;font-family:Arial, sans-serif;font-size:12px;font-weight:bold;overflow:hidden;padding:1px 1px;text-align:center;vertical-align:top;word-break:normal"';
    let td_healthDead = ' style="background-color:#FF0000;border-color:#93a1a1;border-style:solid;border-width:1px;color:#002b36;font-family:Arial, sans-serif;font-size:12px;font-weight:bold;overflow:hidden;padding:1px 1px;text-align:center;vertical-align:top;word-break:normal"';    

    let tr = '';
    let td_hp = '';
    let charDetail = '';
    let charDetail_TOPage = '';
    let charDetail_CSPage = '';
    let charHeader = '';
    let charHeader_CSPage='';

    // **** Load Objects and Arrays of Objects ****
    //let ho_TOReport = getHandout('DM Turnoder List');
    //let ho_TOCharSheet = getHandout('DM Character Sheet')

    let mapXPThresholds = LoadEncounterRating(); //Returns a Map object containing the thresholds table
    let edEasy = 0;
    let edMedium = 0;
    let edHard = 0;
    let edDeadly = 0;
    let edCharLevel = 0;
    let edNPCExpTotal = 0;
    let edNPCExp = 0;
    let edPartyCount = 0;
    let edFoeCount = 0;
    let edDiffMult = 0;
    let edEncounterExp = 0;
    let edDifficulty = '';
    let edColor = '';
    let foeItem = [];

    let tokens = findObjs({
        _type: 'graphic',
        _subtype: 'token',
        // controlledby: '',
        _pageid: Campaign().get('playerpageid')
    }).sort((a, b) => (a.get("name") > b.get("name") ? 1 : -1));

    // Did we advance the turn order?
    let cmd_advance = DidTOAdvance();

    //***************************************************************************************
    //  Build Menu Buttons
    //***************************************************************************************

    // PR, TokenName/Custom, HP/HPMax, AC, PP, Attributes, Speed, Senses, Tooltip, Status
    let cmdRefresh = '&#13;!DMDash --TOReport';
    let btnRefresh = makeMenuButton('REFRESH!', '!DMDash --TOReport');
    let btnExp = '   ' + makeButton(emojiPlus + 'Expand Current Character Details', '!DMDash --TOReport expand');
    let btnCps = '   ' + makeButton(emojiMinus + 'Collapse Current Character Details', '!DMDash --TOReport collapse');

    let btnPrev = makeMenuButton('Prev',`!DMDash --TO-Prev`);
    let btnNext = makeMenuButton('Next',`!DMDash --TO-Next`);
    let btnSort = makeMenuButton('Sort',`!DMDash --TO-Sort`);
    let btnSortWrapped = makeMenuButton('Sort-Wrap',`!DMDash --TO-SortWrapped`);
    let btnClear = makeMenuButton('Clear',`!DMDash --TO-Clear`);

    let btnRound = makeButton('Round', `!DMDash --TO-AddRound`);
    let btnCounter = makeButton('Counter', `!DMDash --TO-AddCountdown ?{Counter direction|Count Down,-1|Count Up,+1} ?{Counter starting position|10} "?{Counter Name|}"`);
    let btnCustom = makeButton('Custom', `!DMDash --TO-AddCustom ?{Custom turnorder position|10} "?{Custom turnorder Name|}"`);
    let btnResetStats = makeButton('Reset Stats', `!DMDash --ResetStats`);
    let btnPlayerStats = makeButton('Player Stats', `!DMDash --PlayerStats`);
    let btnPlayerAccess = makeButton('Player Access', `!DMDash --PlayerAccess`);
    let btnAddNote = makeButton('GMNote', `!DMDash --AddGMNote "?{Session GM Note?|}"`)

    let btnD20 = makeButton('D20', `!${htmlCR}/roll d20 `) + makeButton('-w', `!${htmlCR}/gmroll d20 `);
    let btnAdv = makeButton('Adv', `!${htmlCR}/roll 2d20kh1 `) + makeButton('-w', `!${htmlCR}/gmroll 2d20kh1 `);
    let btnDis = makeButton('Dis', `!${htmlCR}/roll 2d20kl1 `) + makeButton('-w', `!${htmlCR}/gmroll 2d20kl1 `);
    let btnD4 = makeButton('D4', `!${htmlCR}/roll d4 `) + makeButton('-w', `!${htmlCR}/gmroll d4 `);
    let btnD6 = makeButton('D6', `!${htmlCR}/roll d6 `) + makeButton('-w', `!${htmlCR}/gmroll d6 `);
    let btnD8 = makeButton('D8', `!${htmlCR}/roll d8 `) + makeButton('-w', `!${htmlCR}/gmroll d8 `);
    let btnD10 = makeButton('D10', `!${htmlCR}/roll d10 `) + makeButton('-w', `!${htmlCR}/gmroll d10 `);
    let btnD12 = makeButton('D12', `!${htmlCR}/roll d12 `) + makeButton('-w', `!${htmlCR}/gmroll d12 `);
    let btnSaves ='';

    // Look at building logic to dynamically add macros from a Mule, based on a prefix of DMR-
    // let btn1 = makeButton('[Player Macros*]', '!&#13;&#37;{Mule|Player-Macros}', 105);
    // let btn2 = makeButton('[DM Tools]', '!&#13;&#37;{Mule|DM-Tools}', 77);
    // let btn9 = makeButton('[Refresh Markers]', `!&#13;&#37;{Mule|AdjHealthMarkers} ${cmdRefresh}`, 119);
    // let btn10 = makeButton('[Combat*]', `!&#13;&#37;{Mule|Menu-Combat-Macros}`, 63);
    // let btn11 = makeButton('[NPC Tools*]', `!&#13;&#37;{Mule|NPC-Tools}`, 84);
    // let btn12 = makeButton('[AoE*]', `!&#13;&#37;{Mule|AoEMenu}`, 40);
    
    // Build the Menu buttons
    let btns = btnRefresh + " "
    btns += btnNext + " "
    btns += btnPrev + " "
    btns += btnSort + " "
    btns += btnSortWrapped + " "
    btns += btnClear + "  |  " 
    btns += btnRound + " "
    btns += btnCounter + " "
    btns += btnCustom + "  |  " 
    btns += btnD20  + " "
    btns += btnAdv + " "
    btns += btnDis + " "
    btns += btnD4 + " "
    btns += btnD6 + " "
    btns += btnD8+ " "
    btns += btnD10+ " "
    btns += btnD12 + "  |  "  
    // btns += btnAddNote + " "
    btns += btnResetStats + " " 
    btns += btnPlayerStats + " "
    btns += btnPlayerAccess
    //btns = openBox + btns + closeBox
    btns = openMenuBox + btns + closeBox
    
    if(Campaign().get("turnorder")=="") {
      turnorder = [];
    } else {
      toObj = JSON.parse(Campaign().get("turnorder"));
    }
    
    // Is there at least a couple entries in the turn order, and we are responding to an 
    // event that advances the turnorder?
    if (toObj.length >0) { 

      // *************************************************************
      // This section of code calculates the time a player or DM takes
      // on a turn.
      // *************************************************************

      // Turnorder Time Report
      let dt1 = state.DMDashboard.LastDT1;

      // log(`Debug TOAdv 1: cmd_advance:${cmd_advance} dt1:${dt1}`)
      if(isNaN(dt1))
      {
        state.DMDashboard.LastDT1 = getSystemTimeInSecs();
        state.DMDashboard.LastUTCDate = GetSystemUTCDate();
      } else {
        dt2 = getSystemTimeInSecs();
        state.DMDashboard.LastDT1 = dt2;
        state.DMDashboard.LastUTCDate = GetSystemUTCDate();
        dt_diff = Math.floor(Math.abs(Number(dt2) - Number(dt1)));

        // log(`Debug TOAdv 2: dt_diff:${dt_diff} dt2:${dt2}`)
        if (dt_diff > 10000  || cmd_advance == 0){
          dt_diff=0;
        } else {

          // If we got here, then we know that a Next Item event was fired from the TO dialog or
          // through the API

          // Get Previous Turn Id and see if it was a character and we have seconds to apply
          if ((dt_diff >= 0) && toObj[toObj.length-1].id != -1) {

            // Yes, Lets see if it's a PC or NPC
            tType = getTokenType(toObj[toObj.length-1].id); // Returns NPC, CHAR, CUSTOM, UTILITY or OTHER
            // log(`Debug TOAdv 3: tType:${tType}`)

            switch (tType) {

              case 'CHAR':
                let toPrevToken = getObj("graphic", toObj[toObj.length-1].id);
                let toPrevChar = getObj("character", toPrevToken.get('represents'));

                TO_Secs = getAttrByName(toPrevChar.get('_id'),'to_secs','current');
                if (!TO_Secs){TO_Secs=0}

                TO_Count = getAttrByName(toPrevChar.get('_id'),'to_count','current');
                if (!TO_Count){TO_Count=0}

                TO_Avg = getAttrByName(toPrevChar.get('_id'),'to_avg','current');
                if (!TO_Avg){TO_Avg=0}

                y=TO_Secs;

                if (TO_Count >= 50){
                  TO_Count = 50;
                  TO_Secs = Number(TO_Secs) - Number(TO_Avg) + Number(dt_diff);
                } else {
                  TO_Secs = Number(dt_diff) + Number(TO_Secs);
                  TO_Count = Number(TO_Count) + 1;
                }
                // log(`TO_Metrics: Cnt(${TO_Count}) OldSecs(${y}) NewSecs(${TO_Secs}) Avg(${TO_Avg}) dt_diff(${dt_diff})`)

                TO_Avg = Math.floor(TO_Secs / TO_Count);
                changeAttributeValue(toPrevChar.get('_id'), 'to_secs', TO_Secs, 3000)
                changeAttributeValue(toPrevChar.get('_id'), 'to_count', TO_Count, 50)
                changeAttributeValue(toPrevChar.get('_id'), 'to_avg', TO_Avg, 60)
                changeAttributeValue(toPrevChar.get('_id'), 'to_lastturn', dt_diff)
                break;

              case 'NPC':  
              case 'NPC-CHARSHEET':

                // Store NPC turnorder stats in State Memory
                TO_Count = state.DMDashboard.DM_Count;
                TO_Secs = state.DMDashboard.DM_Secs;
                TO_Avg = state.DMDashboard.DM_Avg;

                if (TO_Count >= 50){
                  TO_Count = 50;
                  TO_Secs = TO_Secs - TO_Avg + dt_diff;
                } else {
                  TO_Secs += dt_diff;
                  TO_Count += 1;
                }

                TO_Avg = Math.floor(TO_Secs / TO_Count);
                state.DMDashboard.DM_Secs = TO_Secs;
                state.DMDashboard.DM_Count = TO_Count;
                state.DMDashboard.DM_Avg = TO_Avg;
                state.DMDashboard.DM_LastTurn = dt_diff;
                break;
            }
          }
        }
      }

      reportPerformance('Complete Stats Calcs');

      // *************************************************************
      // Build the Detailed NPC or Character section if expanded 
      // *************************************************************

      //let dashOptions = 'Hit|Miss|Arrow|Door|Trap|Ouch|Boom|Pew|Magic|Steps|Amb1|Amb2'


      let btnHit = makeDBPlayButton('Hit');
      let btnMiss = makeDBPlayButton('Miss');
      let btnArrow = makeDBPlayButton('Arrow');
      let btnDoor = makeDBPlayButton('Door');
      let btnTrap = makeDBPlayButton('Trap');
      let btnOuch = makeDBPlayButton('Ouch');
      let btnBoom = makeDBPlayButton('Boom');
      let btnPew = makeDBPlayButton('Pew');
      let btnMagic = makeDBPlayButton('Magic');
      let btnSteps = makeDBPlayButton('Steps');
      let btnAmb1 = makeDBPlayButton('Amb1');
      let btnAmb2 = makeDBPlayButton('Amb2');

      let trackTbl = ''
      trackTbl = html.table(html.tr(html.td(btnHit,{'vertical-align':'middle', 'border':'0'}) + html.td(btnTrap,{'vertical-align':'middle', 'border':'0'})+ html.td(btnMagic,{'vertical-align':'middle', 'border':'0'}))+
                        html.tr(html.td(btnMiss,{'vertical-align':'middle', 'border':'0'}) + html.td(btnOuch,{'vertical-align':'middle', 'border':'0'}) + html.td(btnSteps,{'vertical-align':'middle', 'border':'0'}))+
                        html.tr(html.td(btnArrow,{'vertical-align':'middle', 'border':'0'}) + html.td(btnBoom,{'vertical-align':'middle', 'border':'0'}) + html.td(btnAmb1,{'vertical-align':'middle', 'border':'0'}))+
                        html.tr(html.td(btnDoor,{'vertical-align':'middle', 'border':'0'}) + html.td(btnPew,{'vertical-align':'middle', 'border':'0'}) + html.td(btnAmb2,{'vertical-align':'middle', 'border':'0'}))
                  , {'border':'0', 'background-color': '#EFEBD6', 'color': 'black'})
      
      charDetail = '';
      tType = getTokenType(toObj[0].id); // Returns NPC, CHAR, CUSTOM, UTILITY or OTHER

      switch (tType) {
        case 'OTHER':

          toToken = getObj("graphic", toObj[0].id);                
          tknImg = `<img style = 'max-height: 60px; max-width: 60px; padding: 0px; margin: 0px !important' src = '${toToken.get('imgsrc')}'</img>`;
          tknImg = addTooltip("Ping Me - GM Only", makeButton(tknImg, `!DMDash --PingToken-GM ${toToken.get('_id')}`));
          charHeader 
          charHeader = `<table style='font-weight:bold; color:#fff; background-color:#404040; margin-right:2px; padding:2px; margin: 0 auto;'><tr><td>${tknImg} ${toToken.get('name')} - Graphic Token (Not associated with Character/NPC)</td> <td>Turnorder: (${toObj[0].pr}) <span id=EncDiff>1</span></td></tr></table>`;
          charHeader =html.table(
                        html.tr(
                          html.td(`${tknImg} ${toToken.get('name')} - Graphic Token (Not associated with Character/NPC)`) + 
                          html.td(trackTbl, {'vertical-align':'middle', 'border':'1'})+
                          html.td(`<span id=EncDiff>1</span>`)), {'background-color': '#404040', 'color': '#fff'})

          charHeader_CSPage = charHeader;
          charDetail = '';
          
          break;
        case 'CUSTOM':
          charHeader = `<table style='border: 0; border-collapse:collapse; font-weight:normal; color:#fff; background-color:#404040; padding: 0; margin: 0 auto;'>`
          charHeader +=   `<tr><td style='vertical-align: middle; text-align: center; padding: 0; border:0'>Custom Turnorder: ${toObj[0].custom} (${toObj[0].pr}) [Formula: ${toObj[0].formula}]</td>`
          charHeader +=       `<td style='vertical-align: middle; text-align: left; padding: 0; border:0'><table style='font-weight:bold; color:#fff; background-color:#404040; margin-right:2px; padding:2px;'><span id=EncDiff>1</span></td>`
          charHeader +=   `</tr></table>`;
          charHeader = html.table(
                        html.tr(
                          html.td(`Custom Turnorder: ${toObj[0].custom} (${toObj[0].pr}) [Formula: ${toObj[0].formula}]`) + 
                          html.td(trackTbl, {'vertical-align':'middle', 'border':'1'})+
                          html.td(`<span id=EncDiff>1</span>`)), {'background-color': '#404040', 'color': '#fff'})

          charHeader_CSPage = charHeader;
          charDetail = '';
          break;
        case 'CHAR':
        case 'NPC-CHARSHEET':
          // log('TOReport: PC');                                    

          toToken = getObj("graphic", toObj[0].id);
          toChar = getObj("character", toToken.get('represents'));

          isnpc = 0;
          tdbase = tdpc;
          charDetail = '';

          TO_Avg = getAttrByName(toChar.get('_id'),'to_avg','current');
          TO_Secs = getAttrByName(toChar.get('_id'),'to_secs','current');
          TO_Count = getAttrByName(toChar.get('_id'),'to_count','current');

          //Header Info
          // charBtn = addTooltip("Open Character Sheet", makeButton('📑', 'https://journal.roll20.net/character/' + toToken.get('represents'), 20));
          tknImg = `<img style = 'max-height: 60px; max-width: 60px; padding: 0px; margin: 0px !important' src = '${toToken.get('imgsrc')}'</img>`;
          tknImg = addTooltip("Ping Me - GM Only", makeButton(tknImg, `!DMDash --PingToken-GM ${toToken.get('_id')}`));

          charHeader = html.table(
                          html.tr(
                            html.td(`${tknImg}`, {'vertical-align':'middle', 'width': '60px', 'border':'0'}) +
                            html.td(html.table( 
                                html.tr(html.td(`<b>${toToken.get('name')}</b>`, {'border':'0'})) +
                                html.tr(html.td(`<i>(${getAttrByName(toChar.get('_id'),'alignment','current')} ${getAttrByName(toChar.get('_id'),'class_display','current')} (${getAttrByName(toChar.get('_id'),'race_display','current')})</i>`, {'border':'0'})) +
                                html.tr(html.td(`<i>Avg Turn: ${TO_Avg}secs Total Secs: ${TO_Secs} Turns: ${TO_Count}</i>`, {'border':'0'})), {'border': '0'}), {'vertical-align':'middle', 'border':'0'}) +
                            html.td(trackTbl, {'vertical-align':'middle', 'border':'1'})+
                            html.td(`<span id=EncDiff>1</span>`, {'vertical-align':'middle', 'border':'0'})), {'background-color': '#404040' , 'color': '#fff'});

          charHeader_CSPage = `<table style='border:0px; border-collapse:collapse; font-weight:normal; color:#fff; background-color:#404040; margin-right:0px; padding:0px;'>`
          charHeader_CSPage += `<tr><td style='width: 100%; vertical-align: middle; text-align: left; padding: 0; border:0'>${tknImg} ${toToken.get('name')}   <i>${getAttrByName(toChar.get('_id'),'alignment','current')} ${getAttrByName(toChar.get('_id'),'class_display','current')} (${getAttrByName(toChar.get('_id'),'race_display','current')})</i>)</td></tr>`
          charHeader_CSPage += `</table>`

          // Load the the cached Detail page (Performance Improvement)
          if (charMap.has(toObj[0].id)) {
            // log('Using Map for: ' + toObj[0].id)
            charMapItem = charMap.get(toObj[0].id)
            charDetail = charMapItem.txt

            // Replace dynamic elements - This is quicker than reading the who character sheet of information again.
            charDetail = replaceDynamicSpanElement(charDetail, 'chardetail-hp', getAttrByName(toChar.get('_id'),'hp','current'))
            let arySL = ['1', '2', '3', '4', '5', '6', '7', '8', '9'];
            arySL.forEach(sl => {
              let sr = getAttrByName(toChar.get('_id'),`lvl${sl}_slots_expended`,'current');
              charDetail = replaceDynamicSpanElement(charDetail, `chardetail-spellslots-${sl}`, sr)
            });

            charDetail_TOPage = openScrollCharBox + charDetail + closeScrollBox;
            charDetail_CSPage = openBox + charDetail + closeBox

            // Done.
            break;
          }

          //Row1 - Column 1-5: Basic Info
          charDetail += `<table><thead><tr>`
          charDetail += `<th${th.slice(0,-1)}width:15%">Attributes</th>`
          charDetail += `<th${th.slice(0,-1)}width:15%">Stats</th>`
          charDetail += `<th${th.slice(0,-1)}width:25%">Skills</th>`
          charDetail += `<th${th.slice(0,-1)}width:12%">Attacks</th>`
          charDetail += `<th${th.slice(0,-1)}width:13%">Traits</th>`
          charDetail += `<th${th.slice(0,-1)}width:20%">Spells/Resources</th>`
          charDetail += `</tr></thead><tbody>`;

          // Send a "Next Turn" message to the game chat, if this is being refreshed for that purpose
          // log('Advance? ' + cmd_advance);
          if (cmd_advance == 1 && state.DMDashboard.TurnNotification == true){
            // sendChat('Turn Order', `${openChat}<table><tr><td>${tknImg}</td><td><b>${toToken.get('name')} is up!</b><br> ATPT: ${TO_Avg} Secs / Last turn: ${getAttrByName(toChar.get('_id'),'to_lastturn','current')})</td></tr></table> ${closeChat}`);
            mySendChat(false, 'Turn Order', `${tknImg}<b>${toToken.get('name')} is up!</b>  <i>ATPT: ${TO_Avg} Secs / Last turn: ${getAttrByName(toChar.get('_id'),'to_lastturn','current')}</i>`)
          }

          //Row2 - Column 1: Basic Info
          charDetail += '<tr><td' + tdpc + '>' // 1 Row Table incapsalating columns of info
          charDetail += '<table>'; // Table for Column 1
          
          charDetail += '<tr><td' + tdpc + '><b>Str:</b></td><td' + tdpc + '>' 
          charDetail += getAttrByName(toChar.get('_id'),'strength','current')
          charDetail += '(' + AddSign(getAttrByName(toChar.get('_id'),'strength_mod','current')) + ')</td><td' + tdpc + '>' 
          charDetail += addTooltip('Strength Saving Throw', makeButton('sv:' + AddSign(getAttrByName(toChar.get('_id'),'strength_save_bonus','current')),`!${htmlCR}${htmlAt}{` + toChar.get('name') + '|strength_save_roll}')) + '</td></tr>';

          charDetail += '<tr><td' + tdpc + '><b>Dex:</b></td><td' + tdpc + '>' 
          charDetail += getAttrByName(toChar.get('_id'),'dexterity','current') 
          charDetail += '(' + AddSign(getAttrByName(toChar.get('_id'),'dexterity_mod','current')) +')</td><td' + tdpc + '>' 
          charDetail += addTooltip('Dexterity Saving Throw', makeButton('sv:' + AddSign(getAttrByName(toChar.get('_id'),'dexterity_save_bonus','current')),`!${htmlCR}${htmlAt}{` + toChar.get('name') + '|dexterity_save_roll}')) + '</td></tr>';
          
          charDetail += '<tr><td' + tdpc + '><b>Con:</b></td><td' + tdpc + '>' 
          charDetail += getAttrByName(toChar.get('_id'),'constitution','current') 
          charDetail += '(' + AddSign(getAttrByName(toChar.get('_id'),'constitution_mod','current')) +')</td><td' + tdpc + '>' 
          charDetail += addTooltip('Constitution Saving Throw', makeButton('sv:' + AddSign(getAttrByName(toChar.get('_id'),'constitution_save_bonus','current')),`!${htmlCR}${htmlAt}{` + toChar.get('name') + '|constitution_save_roll}')) + '</td></tr>';
          
          charDetail += '<tr><td' + tdpc + '><b>Wis:</b></td><td' + tdpc + '>' 
          charDetail += getAttrByName(toChar.get('_id'),'wisdom','current') 
          charDetail += '(' + AddSign(getAttrByName(toChar.get('_id'),'wisdom_mod','current')) +')</td><td' + tdpc + '>' 
          charDetail += addTooltip('Wisdom Saving Throw', makeButton('sv:' + AddSign(getAttrByName(toChar.get('_id'),'wisdom_save_bonus','current')),`!${htmlCR}${htmlAt}{` + toChar.get('name') + '|wisdom_save_roll}')) + '</td></tr>';

          charDetail += '<tr><td' + tdpc + '><b>Int:</b></td><td' + tdpc + '>' 
          charDetail += getAttrByName(toChar.get('_id'),'intelligence','current') 
          charDetail += '(' + AddSign(getAttrByName(toChar.get('_id'),'intelligence_mod','current')) +')</td><td' + tdpc + '>' 
          charDetail += addTooltip('Intelligence Saving Throw', makeButton('sv:' + AddSign(getAttrByName(toChar.get('_id'),'intelligence_save_bonus','current')),`!${htmlCR}${htmlAt}{` + toChar.get('name') + '|intelligence_save_roll}')) + '</td></tr>';

          charDetail += '<tr><td' + tdpc + '><b>Cha:</b></td><td' + tdpc + '>' 
          charDetail += getAttrByName(toChar.get('_id'),'charisma','current') 
          charDetail += '(' + AddSign(getAttrByName(toChar.get('_id'),'charisma_mod','current')) +')</td><td' + tdpc + '>' 
          charDetail += addTooltip('Charisma Saving Throw', makeButton('sv:' + AddSign(getAttrByName(toChar.get('_id'),'charisma_save_bonus','current')),`!${htmlCR}${htmlAt}{` + toChar.get('name') + '|charisma_save_roll}')) + '</td></tr>';

          charDetail += '</table>'
          charDetail += '<table>'; // Table for Bonds/Flaws/...
          charDetail += '<tr><td' + tdpc + '><b>Personality Traits</b></td></tr>'
          charDetail += '<tr><td' + tdpc + '><i>' + getAttrByName(toChar.get('_id'),'personality_traits','current')  + '</i></td></tr>';
          charDetail += '<tr><td' + tdpc + '><b>Ideals</b></td></tr>'
          charDetail += '<tr><td' + tdpc + '><i>' + getAttrByName(toChar.get('_id'),'ideals','current')  + '</i></td></tr>';
          charDetail += '<tr><td' + tdpc + '><b>Bonds</b></td></tr>'
          charDetail += '<tr><td' + tdpc + '><i>' + getAttrByName(toChar.get('_id'),'bonds','current')  + '</i></td></tr>';
          charDetail += '<tr><td' + tdpc + '><b>Flaws</b></td></tr>'
          charDetail += '<tr><td' + tdpc + '><i>' + getAttrByName(toChar.get('_id'),'flaws','current')  + '</i></td></tr>';


          charDetail += '</table></td>' // End of Column 1

          // Basics
          charDetail += '<td' + tdpc + '><table>';
          charDetail += '<tr><td' + tdpc + '><b>AC:</b>' + getAttrByName(toChar.get('_id'),'ac','current') + '</td></tr>';
          charDetail += '<tr><td' + tdpc + '><b>HP:</b><span id=chardetail-hp>' + getAttrByName(toChar.get('_id'),'hp','current') + '</span>/' + getAttrByName(toChar.get('_id'),'HP','max') + ' Temp:<span id=chardetail-hptemp>' + getAttrByName(toChar.get('_id'),'hp_temp','current') + '</span></td></tr>';
          charDetail += '<tr><td' + tdpc + '><b>Speed:</b>' + getAttrByName(toChar.get('_id'),'speed','current') + '</td></tr>';
          charDetail += '<tr><td' + tdpc + '><b>PP:</b>' + getAttrByName(toChar.get('_id'),'passive_wisdom','current') + '</td></tr>';
          charDetail += '<tr><td' + tdpc + '><b>Init:</b>' + AddSign(getAttrByName(toChar.get('_id'),'initiative_bonus','current')) + '</td></tr>';
          charDetail += '<tr><td' + tdpc + '><b>Caster:</b>' + getAttrByName(toChar.get('_id'),'caster_level','current') + '</td></tr>';
          charDetail += '<tr><td' + tdpc + '><b>Spell DC:</b>' + getAttrByName(toChar.get('_id'),'spell_save_dc','current') + '</td></tr>';
          charDetail += '<tr><td' + tdpc + '><b>Spell Attack Bonus:</b>' + AddSign(getAttrByName(toChar.get('_id'),'spell_attack_bonus','current')) + '</td></tr>';
          charDetail += '<tr><td' + tdpc + '><b>Prof Bonus:</b>' + AddSign(getAttrByName(toChar.get('_id'),'pb','current')) + '</td></tr>';
          charDetail += '<tr><td' + tdpc + '><b>Tools:</b>';                               

          // Define the repeating section identifier
          repeatingSection = 'repeating_tool';  //Prefix
          repeatingName = 'toolname'                    //Suffix 

          // Get the values of the repeating section
          repeatingValues = findObjs({_type: "attribute", _characterid: toChar.get('_id')})
            .filter(attribute => attribute.get("name").startsWith(`${repeatingSection}_`) && attribute.get("name").endsWith(`_${repeatingName}`));

          // log('Repeating tools Length:' + repeatingValues.length);

          // Loop through the values in the repeating section
          for (let rowId in repeatingValues) {
            row = repeatingValues[rowId];
            repeatingField=row.get('name').slice(0,-9);
            charDetail +=  row.get('current') + ', ';
          }
          charDetail += '</td></tr>'; 

          // Need to get
          charDetail += '<tr><td' + tdpc + '><b>Lang/Other:</b>';                               

          // Define the repeating section identifier
          repeatingSection = 'repeating_proficiencies';  //Prefix
          repeatingName = 'name'                    //Suffix 

          // Get the values of the repeating section
          repeatingValues = findObjs({_type: "attribute", _characterid: toChar.get('_id')})
            .filter(attribute => attribute.get("name").startsWith(`${repeatingSection}_`) && attribute.get("name").endsWith(`_${repeatingName}`));

          // Loop through the values in the repeating section
          for (let rowId in repeatingValues) {
            row = repeatingValues[rowId];
            repeatingField=row.get('name').slice(0,-5);
            charDetail +=  row.get('current') + ', ';
          }
          charDetail += '</td></tr>'; 
          charDetail += '</table></td>';

          // Skills
          charDetail += '<td' + tdpc + '><table><tr><td' + tdpc + '><table>';
          skills = ['acrobatics', 'animal_handling', 'arcana', 'athletics', 'deception', 'history', 'insight', 'intimidation', 'investigation', 'medicine', 'nature', 'perception', 'performance', 'persuasion', 'religion', 'sleight_of_hand', 'stealth', 'survival'];
          skills.forEach(mySkill => {
              let skill = resolveAttr(toChar.get('_id'), mySkill + '_bonus');
              let skill_value = skill.current;
              let skill_name = mySkill;
              charDetail += '<tr><td' + tdpc + '><b>' + makeButton(skill_name,`!${htmlCR}${htmlAt}{${toChar.get('name')}|${mySkill}_roll}`)+ ':</b>' + AddSign(skill_value) + '</td></tr>';
              if (mySkill == 'investigation'){
                  charDetail += '</table></td><td' + tdpc + '><table>';
              }
          });

          charDetail += '</table></td></table></td>';

          // *******   Attacks *******
          charDetail += '<td' + tdpc + '><table>';

          // Define the repeating section identifier
          repeatingSection = 'repeating_attack';  //Prefix
          repeatingName = 'atkname'               //Suffix 

          // Get the values of the repeating section
          repeatingValues = findObjs({_type: "attribute", _characterid: toChar.get('_id')})
            .filter(attribute => attribute.get("name").startsWith(`${repeatingSection}_`) && attribute.get("name").endsWith(`_${repeatingName}`));

          // Loop through the values in the repeating section
          for (let rowId in repeatingValues) {
            row = repeatingValues[rowId];
            // log('Repeating attack info: ' + rowId + '.' + row.get('name') + ': ' +  row.get('current'));

            repeatingField = row.get('name').slice(0,-8);
            let repeatingAction = repeatingField + '_attack';
            repeatingAction = makeButton(row.get('current'), `!${htmlCR}${htmlPct}{${toChar.get('_id')}|${repeatingAction}}`, 125);
            charDetail += '<tr><td' + tdpc + '>' + repeatingAction + '</td></tr>';
          }

          // ******* Traits *******
          charDetail += '</table></td>';
          charDetail += '<td' + tdpc + '><table>';

          // Define the repeating section identifier
          repeatingSection = 'repeating_traits';  //Prefix
          repeatingName = 'name'                    //Suffix 

          // Get the values of the repeating section
          repeatingValues = findObjs({_type: "attribute", _characterid: toChar.get('_id')})
            .filter(attribute => attribute.get("name").startsWith(`${repeatingSection}_`) && attribute.get("name").endsWith(`_${repeatingName}`));

          // Loop through the values in the repeating section
          for (let rowId in repeatingValues) {
            row = repeatingValues[rowId];
            repeatingField=row.get('name').slice(0,-5);
            let repeatingDesc = decodeUnicode(getAttrByName(toChar.get('_id'),repeatingField + '_description','current'))
            let traitSource = getAttrByName(toChar.get('_id'),repeatingField + '_source','current') + ': ' + getAttrByName(toChar.get('_id'),repeatingField + '_source_type','current')
            let rtRoll = makeButton(row.get('current'), buildTraitOutput(toChar.get('name'), row.get('current'), traitSource, repeatingDesc));
            let ttDiv = addTooltip(repeatingDesc, rtRoll)

            charDetail += '<tr><td' + tdpc + '>' + ttDiv + '</td></tr>';
          }
          charDetail += '</table></td>';
          charDetail += '<td' + tdpc + '><table>';

          // ************* Spells *******************
          spell_lvls = ['cantrip', '1', '2', '3', '4', '5', '6', '7', '8', '9'];
          slots = '';
          slots_remaining = '';

          spell_lvls.forEach(spell_lvl => {

            // Get Slots and Prepared
            slots = '';
            if (spell_lvl != 'cantrip') {
              slots = getAttrByName(toChar.get('_id'),`lvl${spell_lvl}_slots_total`,'current');
              slots_remaining = getAttrByName(toChar.get('_id'),`lvl${spell_lvl}_slots_expended`,'current');
              slots = `(<span id=chardetail-spellslots-${spell_lvl}>${slots_remaining}</span> of ${slots})`;
            }

            repeatingSection = `repeating_spell-${spell_lvl}`;  //Prefix
            repeatingName = 'spellname'                    //Suffix 

            // Get the values of the repeating section
            repeatingValues = findObjs({_type: "attribute", _characterid: toChar.get('_id')})
              .filter(attribute => attribute.get("name").startsWith(`${repeatingSection}`) && attribute.get("name").endsWith(`_${repeatingName}`));
            if (repeatingValues.length > 0){
              charDetail += '<tr><td' + tdpc + '><b>Level ' + spell_lvl + slots + ':</b> ';
              // Loop through the values in the repeating section
              for (let rowId in repeatingValues) {
                row = repeatingValues[rowId];
                repeatingField=row.get('name').slice(0,-10);
                let repeatingAction = repeatingField + '_spell';
                let spellPrepared = 0;
                if (spell_lvl != 'cantrip') { spellPrepared = getAttrByName(toChar.get('_id'),`${repeatingField}_spellprepared`,'current'); }
                
                let spellInnate = getAttrByName(toChar.get('_id'),`${repeatingField}_innate`,'current');
                if (spellInnate.length > 2) { spellInnate = '<i>(' + spellInnate + ')</i>'; } else {spellInnate = ''; }

                repeatingAction = '<div style="border: 1px solid #dcdcdc; padding: 1px; display: inline-block;">' + makeButton(row.get('current'), `!${htmlCR}${htmlAt}{${toChar.get('_id')}|${repeatingAction}}`);

                // let spellLink = 'https://www.dndbeyond.com/spells/' + row.get('current').replace('/ /g', "-")
                let spellLink = 'https://app.roll20.net/compendium/dnd5e/Spells:'+encodeURIComponent(row.get('current'))+'#content'
                // log(`spellLink: ${row.get('current')} ${spellLink}`)                                
                spellLink = makeButton(emojiLink, spellLink, 15)

                // Build a spell description field here (Range, Action, Target, Duration, Con, Desc)
                if (spell_lvl != 'cantrip') {
                  if (spellPrepared == '1') {
                    charDetail += repeatingAction + '*' + spellInnate + spellLink + '</div>,';  
                  } else {
                    charDetail += repeatingAction + ' ' + spellInnate + spellLink + '</div>,';  
                  }

                } else {
                  charDetail += repeatingAction + ' ' + spellInnate + spellLink +  '</div>,';    
                }

              }
              charDetail = charDetail.slice(0,-2);
              charDetail += '</td></tr>';
            }
          });

          if (false) { //Skipping this section right now to speed up execution
            // ********** Resources ***************
            charDetail += '<tr><td' + tdpc + '>  </td></tr><tr><td' + tdpc + '><br><b><u> ***** Resources ***** </u></b> </td></tr>';
            // charDetail += '<td' + tdpc + '><table>';
            charDetail += '<tr><td' + tdpc + '><b>' + getAttrByName(toChar.get('_id'),'class_resource_name','current') + '[cr] (' + getAttrByName(toChar.get('_id'),'class_resource','current') + ' of ' + getAttrByName(toChar.get('_id'),'class_resource','max') + ')</td></tr>';
            charDetail += '<tr><td' + tdpc + '><b>' + getAttrByName(toChar.get('_id'),'other_resource_name','current') + '[or] (' + getAttrByName(toChar.get('_id'),'other_resource','current') + ' of ' + getAttrByName(toChar.get('_id'),'other_resource','max') + ')</td></tr>';

            // Define the repeating section identifier
            repeatingSection = 'repeating_resource';  //Prefix
            repeatingName = 'resource_left_name';  //Suffix 
            repeatingValue = 0;
            repeatingMax = 0;

            // Get the values of the repeating section
            repeatingValues = findObjs({_type: "attribute", _characterid: toChar.get('_id')})
              .filter(attribute => attribute.get("name").startsWith(`${repeatingSection}_`) && attribute.get("name").endsWith(`_${repeatingName}`));

            // Loop through the values in the repeating section
            for (let rowId in repeatingValues) {
              row = repeatingValues[rowId];
              repeatingName = getAttrByName(toChar.get('_id'),row.get('name'),'current')
              repeatingField = row.get('name').slice(0,-19);
              repeatingValue = getAttrByName(toChar.get('_id'),repeatingField + '_resource_left','current')
              repeatingMax = getAttrByName(toChar.get('_id'),repeatingField + '_resource_left','current')
              charDetail += '<tr><td' + tdpc + '><b>' + row.get('current') + '</b>: (' + repeatingValue + ' of ' + repeatingMax + ')</td></tr>';
            }

            repeatingSection = 'repeating_resource';  //Prefix
            repeatingName = 'resource_right_name';  //Suffix 
            repeatingValue = 0;
            repeatingMax = 0;

            // Get the values of the repeating section
            repeatingValues = findObjs({_type: "attribute", _characterid: toChar.get('_id')})
              .filter(attribute => attribute.get("name").startsWith(`${repeatingSection}_`) && attribute.get("name").endsWith(`_${repeatingName}`));

            // Loop through the values in the repeating section
            for (let rowId in repeatingValues) {
              let row = repeatingValues[rowId];
              let repeatingName = getAttrByName(toChar.get('_id'),row.get('name'),'current')
              let repeatingField = row.get('name').slice(0,-20);
              let repeatingValue = getAttrByName(toChar.get('_id'),repeatingField + '_resource_right','current')
              let repeatingMax = getAttrByName(toChar.get('_id'),repeatingField + '_resource_right','current')
              charDetail += '<tr><td' + tdpc + '><b>' + row.get('current') + '</b>: (' + repeatingValue + ' of ' + repeatingMax + ')</td></tr>';
            }
          }

          charDetail += '</table></td></tr></table>';
          //charDetail = openScrollCharBox + charDetail + closeScrollBox;
          charDetail_TOPage = openScrollCharBox + charDetail + closeScrollBox;
          charDetail_CSPage = openBox + charDetail + closeBox

          // log('Loading Char int Map Object: ' + toObj[0].id);
          //charMap.set(toChar.get('id'), {id: toChar.get('id'), charId: toChar.get('id'), txt: charDetail})
          charMap.set(toObj[0].id, {id: toObj[0].id, charId: toChar.get('id'), txt: charDetail});

          break;
        case 'NPC':
          // log('TOReport: NPC');                                    

          toToken = getObj("graphic", toObj[0].id);
          toChar = getObj("character", toToken.get('represents'));

          isnpc = 1;
          tdbase = tdnpc;

          // Header 
          // charBtn = makeButton('📑', 'https://journal.roll20.net/character/' + toToken.get('represents'), 20);
          tknImg = `<img style = 'max-height: 60px; max-width: 60px; padding:0 0px; margin: 0px !important' src = '${toToken.get('imgsrc')}'</img>`;
          tknImg = addTooltip("Ping Me - GM Only", makeButton(tknImg, `!DMDash --PingToken-GM ${toToken.get('_id')}`));

          charHeader = `<table style='border: 0; border-collapse:collapse; font-weight:normal; color:#fff; background-color:#404040; padding: 0; margin: 0 auto;'>`
          charHeader +=   `<tr><td style='vertical-align: middle; text-align: center; padding: 0; border:0'>`
          charHeader +=       `<table style='border: 0; border-collapse: collapse; font-weight:normal; color:#fff; background-color: #404040; padding: 0; margin: 0 auto;'>`
          charHeader +=           `<tr><td style='text-align: center; padding: 0; border:0;'>${tknImg} ${toToken.get('name')} <i>(${getAttrByName(toChar.get('_id'),'npc_type','current')})</i></td></tr>`
          charHeader +=           `<tr><td style='text-align: center; padding: 0; border:0;'></td></tr>`
          charHeader +=           `<tr><td style='text-align: center; padding: 0; border:0;'><i>Avg Turn: ${state.DMDashboard.DM_Avg}secs Total Secs: ${state.DMDashboard.DM_Secs} Turns: ${state.DMDashboard.DM_Count}</i></td></tr>`
          charHeader +=       `</table></td>` 
          charHeader +=   `<td style='vertical-align: bottom; text-align: left; padding: 0; border:0'><span id=EncDiff>1</span></td></tr></table>`

          charHeader = html.table(
            html.tr(
              html.td(`${tknImg}`, {'vertical-align':'middle', 'width': '60px', 'border':'0'}) + 
              html.td(html.table( 
                  html.tr(html.td(`<b>${toToken.get('name')}</b>`, {'border':'0'})) +
                  html.tr(html.td(`<i>(${getAttrByName(toChar.get('_id'),'npc_type','current')})</i>`, {'border':'0'})) +
                  html.tr(html.td(`<i>Avg Turn: ${TO_Avg}secs Total Secs: ${TO_Secs} Turns: ${TO_Count}</i>`, {'border':'0'})), {'border':'0'}), {'vertical-align':'middle', 'border':'0'}) +
              html.td(trackTbl, {'vertical-align':'middle', 'border':'1'})+
              html.td(`<span id=EncDiff>1</span>`,{'vertical-align':'middle', 'border':'0'} )), {'background-color': '#404040', 'color': '#fff'});

              /* 
              charHeader = html.table(
                html.tr(
                  html.td(`${tknImg}`, {'vertical-align':'middle', 'width': '60px', 'border':'0'}) + html.td(
                    html.table( 
                      html.tr(html.td(`<b>${toToken.get('name')}</b>`, {'border':'0'})) +
                      html.tr(html.td(`<i>(${getAttrByName(toChar.get('_id'),'alignment','current')} ${getAttrByName(toChar.get('_id'),'class_display','current')} (${getAttrByName(toChar.get('_id'),'race_display','current')})</i>`, {'border':'0'})) +
                      html.tr(html.td(`<i>Avg Turn: ${TO_Avg}secs Total Secs: ${TO_Secs} Turns: ${TO_Count}</i>`, {'border':'0'})), {'border': '0'}), {'vertical-align':'middle', 'border':'0'}) +
                  html.td(`<span id=EncDiff>1</span>`, {'vertical-align':'middle', 'border':'0'})), {'background-color': '#404040' , 'color': '#fff'});
              */


          charHeader_CSPage = `<table style='border-collapse:collapse; font-weight:bold; color:#fff; background-color:#404040; margin-right:2px; padding:2px; margin: 0 auto;'>`
          charHeader_CSPage += `<tr><td style="width: 100%;vertical-align: middle;"> ${tknImg} ${toToken.get('name')}    <i>(${getAttrByName(toChar.get('_id'),'npc_type','current')})</i></td></tr>`
          charHeader_CSPage += `</table>`

          if (charMap.has(toObj[0].id)) {
              // log('Using Map for: ' + toObj[0].id)
              charMapItem = charMap.get(toObj[0].id)
              charDetail = charMapItem.txt
              charDetail = replaceDynamicSpanElement(charDetail, 'chardetail-hp', toToken.get(`bar${state.DMDashboard.HPBar}_value`))

              charDetail_TOPage = openScrollCharBox + charDetail + closeScrollBox;
              charDetail_CSPage = openBox + charDetail + closeBox
              break;
          }

          //Row1 - Header Column
          charDetail += `<table style="border-collapse:collapse; font-weight:bold; color:#fff; background-color:#404040; margin-right:2px; padding:2px; margin: 0 auto;">`
          charDetail += `<thead><tr><th${th.slice(0,-1)}width:15%">Attributes</th>`
          charDetail += `<th${th.slice(0,-1)}width:20%">Stats</th>`
          charDetail += `<th${th.slice(0,-1)}width:20%">Info</th>`
          charDetail += `<th${th.slice(0,-1)}width:20%">Traits/Actions</th>`
          charDetail += `<th${th.slice(0,-1)}width:40%">Spells</th></tr></thead><tbody>`;

          //Row2 - Column 1: Basic Info
          charDetail += '<tr><td' + tdnpc + '>'
          charDetail += `<table style="border-collapse:collapse; font-weight:bold; color:#fff; background-color:#404040; margin-right:2px; padding:2px; margin: 0 auto;">`;
          
          charDetail += '<tr><td' + tdnpc + '><b>Str:</b></td><td' + tdnpc + '>' 
          charDetail += getAttrByName(toChar.get('_id'),'strength','current')
          charDetail += '(' + AddSign(getAttrByName(toChar.get('_id'),'strength_mod','current')) + ')</td><td' + tdnpc + '>' 
          charDetail += addTooltip('Strength Saving Throw', makeButton('sv:' + AddSign(getAttrByName(toChar.get('_id'),'strength_save_bonus','current')),`!${htmlCR}${htmlAt}{` + toChar.get('name') + '|strength_save_roll}')) + '</td></tr>';

          charDetail += '<tr><td' + tdnpc + '><b>Dex:</b></td><td' + tdnpc + '>' 
          charDetail += getAttrByName(toChar.get('_id'),'dexterity','current') 
          charDetail += '(' + AddSign(getAttrByName(toChar.get('_id'),'dexterity_mod','current')) +')</td><td' + tdnpc + '>' 
          charDetail += addTooltip('Dexterity Saving Throw', makeButton('sv:' + AddSign(getAttrByName(toChar.get('_id'),'dexterity_save_bonus','current')),`!${htmlCR}${htmlAt}{` + toChar.get('name') + '|dexterity_save_roll}')) + '</td></tr>';
          
          charDetail += '<tr><td' + tdnpc + '><b>Con:</b></td><td' + tdnpc + '>' 
          charDetail += getAttrByName(toChar.get('_id'),'constitution','current') 
          charDetail += '(' + AddSign(getAttrByName(toChar.get('_id'),'constitution_mod','current')) +')</td><td' + tdnpc + '>' 
          charDetail += addTooltip('Constitution Saving Throw', makeButton('sv:' + AddSign(getAttrByName(toChar.get('_id'),'constitution_save_bonus','current')),`!${htmlCR}${htmlAt}{` + toChar.get('name') + '|constitution_save_roll}')) + '</td></tr>';
          
          charDetail += '<tr><td' + tdnpc + '><b>Wis:</b></td><td' + tdnpc + '>' 
          charDetail += getAttrByName(toChar.get('_id'),'wisdom','current') 
          charDetail += '(' + AddSign(getAttrByName(toChar.get('_id'),'wisdom_mod','current')) +')</td><td' + tdnpc + '>' 
          charDetail += addTooltip('Wisdom Saving Throw', makeButton('sv:' + AddSign(getAttrByName(toChar.get('_id'),'wisdom_save_bonus','current')),`!${htmlCR}${htmlAt}{` + toChar.get('name') + '|wisdom_save_roll}')) + '</td></tr>';

          charDetail += '<tr><td' + tdnpc + '><b>Int:</b></td><td' + tdnpc + '>' 
          charDetail += getAttrByName(toChar.get('_id'),'intelligence','current') 
          charDetail += '(' + AddSign(getAttrByName(toChar.get('_id'),'intelligence_mod','current')) +')</td><td' + tdnpc + '>' 
          charDetail += addTooltip('Intelligence Saving Throw', makeButton('sv:' + AddSign(getAttrByName(toChar.get('_id'),'intelligence_save_bonus','current')),`!${htmlCR}${htmlAt}{` + toChar.get('name') + '|intelligence_save_roll}')) + '</td></tr>';

          charDetail += '<tr><td' + tdnpc + '><b>Cha:</b></td><td' + tdnpc + '>' 
          charDetail += getAttrByName(toChar.get('_id'),'charisma','current') 
          charDetail += '(' + AddSign(getAttrByName(toChar.get('_id'),'charisma_mod','current')) +')</td><td' + tdnpc + '>' 
          charDetail += addTooltip('Charisma Saving Throw', makeButton('sv:' + AddSign(getAttrByName(toChar.get('_id'),'charisma_save_bonus','current')),`!${htmlCR}${htmlAt}{` + toChar.get('name') + '|charisma_save_roll}')) + '</td></tr>';

          // charDetail += '<tr><td' + tdnpc + '><b>Str:</b></td><td' + tdnpc + '>' + getAttrByName(toChar.get('_id'),'strength','current') + '(' + AddSign(getAttrByName(toChar.get('_id'),'strength_mod','current')) +')</td></tr>';
          // charDetail += '<tr><td' + tdnpc + '><b>Dex:</b></td><td' + tdnpc + '>' + getAttrByName(toChar.get('_id'),'dexterity','current')  + '(' + AddSign(getAttrByName(toChar.get('_id'),'dexterity_mod','current')) +')</td></tr>';
          // charDetail += '<tr><td' + tdnpc + '><b>Con:</b></td><td' + tdnpc + '>' + getAttrByName(toChar.get('_id'),'constitution','current') + '(' + AddSign(getAttrByName(toChar.get('_id'),'constitution_mod','current')) +')</td></tr>';
          // charDetail += '<tr><td' + tdnpc + '><b>Wis:</b></td><td' + tdnpc + '>' + getAttrByName(toChar.get('_id'),'wisdom','current') + '(' + AddSign(getAttrByName(toChar.get('_id'),'wisdom_mod','current')) +')</td></tr>';
          // charDetail += '<tr><td' + tdnpc + '><b>Int:</b></td><td' + tdnpc + '>' + getAttrByName(toChar.get('_id'),'intelligence','current') + '(' + AddSign(getAttrByName(toChar.get('_id'),'intelligence_mod','current')) +')</td></tr>';
          // charDetail += '<tr><td' + tdnpc + '><b>Cha:</b></td><td' + tdnpc + '>' + getAttrByName(toChar.get('_id'),'charisma','current') + '(' + AddSign(getAttrByName(toChar.get('_id'),'charisma_mod','current')) +')</td></tr>';
          charDetail += '</table></td>'

          // Basics
          charDetail += `<td${tdnpc}><table style="border-collapse:collapse; font-weight:bold; color:#fff; background-color:#404040; margin-right:2px; padding:2px; margin: 0 auto;">`;
          charDetail += '<tr><td' + tdnpc + '><b>AC:</b>' + getAttrByName(toChar.get('_id'),'npc_ac','current') + ' (' + getAttrByName(toChar.get('_id'),'npc_actype','current') + ')</td></tr>';
          charDetail += '<tr><td' + tdnpc + '><b>HP:</b><span id=chardetail-hp>' + toToken.get(`bar${state.DMDashboard.HPBar}_value`) + '</span>/' + toToken.get(`bar${state.DMDashboard.HPBar}_max`) + '(' + getAttrByName(toChar.get('_id'),'npc_hpformula','current') + ')</td></tr>';
          charDetail += '<tr><td' + tdnpc + '><b>Speed:</b>' + getAttrByName(toChar.get('_id'),'npc_speed','current') + '</td></tr>';
          charDetail += '<tr><td' + tdnpc + '><b>PP:</b>' + getAttrByName(toChar.get('_id'),'passive_wisdom','current') + '</td></tr>';
          charDetail += '<tr><td' + tdnpc + '><b>Init:</b>' + AddSign(getAttrByName(toChar.get('_id'),'initiative_bonus','current')) + '</td></tr>';
          charDetail += '<tr><td' + tdnpc + '><b>Caster:</b>' + getAttrByName(toChar.get('_id'),'caster_level','current') + '</td></tr>';
          charDetail += '<tr><td' + tdnpc + '><b>Spell DC:</b>' + getAttrByName(toChar.get('_id'),'spell_save_dc','current') + '</td></tr>';
          charDetail += '<tr><td' + tdnpc + '><b>Spell Attack Bonus:</b>' + AddSign(getAttrByName(toChar.get('_id'),'spell_attack_bonus','current')) + '</td></tr>';
          charDetail += '</table></td>';

          // Skills
          charDetail += '<td' + tdnpc + '>'
          charDetail += `<table style="border-collapse:collapse; font-weight:bold; color:#fff; background-color:#404040; margin-right:2px; padding:2px; margin: 0 auto;">`;
          charDetail += '<tr><td' + tdnpc + '><b>Skills:</b>';
          skills = ['acrobatics', 'animal_handling', 'arcana', 'athletics', 'deception', 'history', 'insight', 'intimidation', 'investigation', 'medicine', 'nature', 'perception', 'performance', 'persuasion', 'religion', 'sleight_of_hand', 'stealth', 'survival'];
          skills.forEach(mySkill => {
            let skill = resolveAttr(toChar.get('_id'), 'npc_' + mySkill + '_base');
            let skill_value = skill.current;
            if (skill_value != 0) {
              let myRoll = `!${htmlCR}${htmlAt}${htmlRBrace}${toChar.get('name')}|wtype${htmlRBrace}&amp;${htmlLBrace}template:npc${htmlRBrace}&nbsp;${htmlAt}${htmlLBrace}${toChar.get('name')}|npc_name_flag}&nbsp;${htmlAt}${htmlLBrace}${toChar.get('name')}|rtype${htmlRBrace}+[[${htmlAt}{${toChar.get('name')}|npc_${mySkill}}]]]]${htmlRBrace}${htmlRBrace} ${htmlLBrace}${htmlLBrace}r1=[[@${htmlLBrace}${toChar.get('name')}|d20${htmlRBrace}+[[@${htmlLBrace}${toChar.get('name')}|npc_${mySkill}}]]]]${htmlRBrace}${htmlRBrace} ${htmlLBrace}${htmlLBrace}mod=[[[[@{${toChar.get('name')}|npc_${mySkill}}]]]]${htmlRBrace}${htmlRBrace} ${htmlLBrace}${htmlLBrace}rname=${mySkill}${htmlRBrace}${htmlRBrace} ${htmlLBrace}${htmlLBrace}type=Skill${htmlRBrace}${htmlRBrace}`
              charDetail += makeButton(mySkill, myRoll) + ' ' + skill_value + ', ';
            }
          });
          charDetail += '</td></tr>';
          charDetail += '<tr><td' + tdnpc + '><b>Dmg Res:</b>' + getAttrByName(toChar.get('_id'),'npc_resistances','current') + '</td></tr>';
          charDetail += '<tr><td' + tdnpc + '><b>Dmg Imm:</b>' + getAttrByName(toChar.get('_id'),'npc_immunities','current') + '</td></tr>';
          charDetail += '<tr><td' + tdnpc + '><b>Cnd Imm:</b>' + getAttrByName(toChar.get('_id'),'npc_condition_immunities','current') + '</td></tr>';
          charDetail += '<tr><td' + tdnpc + '><b>Dmg Vul:</b>' + getAttrByName(toChar.get('_id'),'npc_vulnerabilities','current') + '</td></tr>';
          charDetail += '<tr><td' + tdnpc + '><b>Lanuages:</b>' + getAttrByName(toChar.get('_id'),'npc_languages','current') + '</td></tr>';
          charDetail += '<tr><td' + tdnpc + '><b>Senses:</b>' + getAttrByName(toChar.get('_id'),'npc_senses','current') + '</td></tr>';
          charDetail += '<tr><td' + tdnpc + '><b>CR:</b>' + getAttrByName(toChar.get('_id'),'npc_challenge','current') + '</td></tr>';
          charDetail += '<tr><td' + tdnpc + '><b>Prof Bonus:</b>' + AddSign(getAttrByName(toChar.get('_id'),'npc_pb','current')) + '</td></tr>';
          charDetail += '</table></td>';
          charDetail += '<td' + tdnpc + '>';
          charDetail += `<table style="border-collapse:collapse; font-weight:bold; color:#fff; background-color:#404040; margin-right:2px; padding:2px; margin: 0 auto;">`;

          // Define the repeating section identifier
          repeatingSection = 'repeating_npctrait';  //Prefix
          repeatingName = 'name'                    //Suffix 

          // Get the values of the repeating section
          repeatingValues = findObjs({_type: "attribute", _characterid: toChar.get('_id')})
            .filter(attribute => attribute.get("name").startsWith(`${repeatingSection}_`) && attribute.get("name").endsWith(`_${repeatingName}`));

          // Loop through the values in the repeating section
          for (let rowId in repeatingValues) {
            row = repeatingValues[rowId];
            // log('Repeating Traits info: ' + rowId + '.' + row.get('name') + ': ' +  row.get('current'));

            repeatingField=row.get('name').slice(0,-5);
            let repeatingDesc = getAttrByName(toChar.get('_id'),repeatingField + '_description','current')
            let traitSource = toChar.get('name')
            let rtRoll = makeButton('T: '+ row.get('current'), buildTraitOutput(toChar.get('name'), row.get('current'), traitSource, repeatingDesc));
            let ttDiv = addTooltip(repeatingDesc, rtRoll)
            charDetail += '<tr><td' + tdnpc + '>'+ ttDiv + '</td></tr>';
          }

          // ******* Actions *******
          // Define the repeating section identifier
          repeatingSection = 'repeating_npcaction';  //Prefix
          repeatingName = 'name'                    //Suffix 

          // Get the values of the repeating section
          repeatingValues = findObjs({_type: "attribute", _characterid: toChar.get('_id')})
            .filter(attribute => attribute.get("name").startsWith(`${repeatingSection}_`) && attribute.get("name").endsWith(`_${repeatingName}`));

          // Loop through the values in the repeating section
          for (let rowId in repeatingValues) {
            row = repeatingValues[rowId];
            repeatingField=row.get('name').slice(0,-5);
            let repeatingAction = repeatingField + '_npc_action';
            repeatingAction = makeButton('A: ' + row.get('current'), `!&#13;&#37;{${toChar.get('_id')}|${repeatingAction}}`);
            charDetail += '<tr><td' + tdnpc + '>' + repeatingAction + '</td></tr>';
          }

          // ***  Bonus Actions *** 
          // Define the repeating section identifier
          repeatingSection = 'repeating_npcbonusaction';  //Prefix
          repeatingName = 'name'                    //Suffix 

          // Get the values of the repeating section
          repeatingValues = findObjs({_type: "attribute", _characterid: toChar.get('_id')})
            .filter(attribute => attribute.get("name").startsWith(`${repeatingSection}_`) && attribute.get("name").endsWith(`_${repeatingName}`));

          // Loop through the values in the repeating section
          for (let rowId in repeatingValues) {
            row = repeatingValues[rowId];
            repeatingField=row.get('name').slice(0,-5);
            let repeatingAction = repeatingField + '_npc_action';
            repeatingAction = makeButton('BA: ' + row.get('current'), `!&#13;&#37;{${toChar.get('_id')}|${repeatingAction}}`);
            charDetail += '<tr><td' + tdnpc + '>' + repeatingAction + '</td></tr>';
          }

          // Reactions
          repeatingSection = 'repeating_npcreaction';  //Prefix
          repeatingName = 'name'                    //Suffix 

          // Get the values of the repeating section
          repeatingValues = findObjs({_type: "attribute", _characterid: toChar.get('_id')})
            .filter(attribute => attribute.get("name").startsWith(`${repeatingSection}_`) && attribute.get("name").endsWith(`_${repeatingName}`));

          // Loop through the values in the repeating section
          for (let rowId in repeatingValues) {
            row = repeatingValues[rowId];
            repeatingField=row.get('name').slice(0,-5);
            //let repeatingAction = repeatingField + '_npc_action';
            //repeatingAction = makeButton('RA: ' + row.get('current'), `!&#13;&#37;{${toChar.get('_id')}|${repeatingAction}}`);
            //charDetail += '<tr><td' + tdnpc + '>' + repeatingAction + '</td></tr>';            
            let repeatingDesc = getAttrByName(toChar.get('_id'),repeatingField + '_description','current')
            let traitSource = toChar.get('name')
            let rtRoll = makeButton('RA: '+ row.get('current'), buildTraitOutput(toChar.get('name'), row.get('current'), traitSource, repeatingDesc));
            let ttDiv = addTooltip(repeatingDesc, rtRoll)
            charDetail += '<tr><td' + tdnpc + '>'+ ttDiv + '</td></tr>';

          }

          // Legendary Actions
          repeatingSection = 'repeating_npcaction-l';  //Prefix
          repeatingName = 'name'                    //Suffix 

          // Get the values of the repeating section
          repeatingValues = findObjs({_type: "attribute", _characterid: toChar.get('_id')})
            .filter(attribute => attribute.get("name").startsWith(`${repeatingSection}_`) && attribute.get("name").endsWith(`_${repeatingName}`));

          // Loop through the values in the repeating section
          for (let rowId in repeatingValues) {
            row = repeatingValues[rowId];
            repeatingField=row.get('name').slice(0,-5);
            let repeatingAction = repeatingField + '_npc_action';
            repeatingAction = makeButton('LA: ' + row.get('current'), `!&#13;&#37;{${toChar.get('_id')}|${repeatingAction}}`);
            charDetail += '<tr><td' + tdnpc + '>' + repeatingAction + '</td></tr>';
          }

          // ************* Spells *******************
          spell_lvls = ['cantrip', '1', '2', '3', '4', '5', '6', '7', '8', '9'];
          charDetail += '</table></td>';
          charDetail += '<td' + tdnpc + '>'
          charDetail += `<table style="border-collapse:collapse; font-weight:bold; color:#fff; background-color:#404040; margin-right:2px; padding:2px; margin: 0 auto;">`;

          spell_lvls.forEach(spell_lvl => {

            repeatingSection = `repeating_spell-${spell_lvl}`;  //Prefix
            repeatingName = 'spellname'                    //Suffix 

            // Get the values of the repeating section
            repeatingValues = findObjs({_type: "attribute", _characterid: toChar.get('_id')})
              .filter(attribute => attribute.get("name").startsWith(`${repeatingSection}`) && attribute.get("name").endsWith(`_${repeatingName}`));
            if (repeatingValues.length > 0){
              charDetail += '<tr><td' + tdnpc + '><b>Level ' + spell_lvl + ':</b> ';
              // Loop through the values in the repeating section
              for (let rowId in repeatingValues) {
                row = repeatingValues[rowId];
                repeatingField=row.get('name').slice(0,-10);
                let repeatingAction = repeatingField + '_spell';

                repeatingAction = '<div style="border: 1px solid #dcdcdc; padding: 1px; display: inline-block;">' + makeButton(row.get('current'), `!&#13;&#37;{${toChar.get('_id')}|${repeatingAction}}`);
                // let spellLink = 'https://www.dndbeyond.com/spells/' + row.get('current').replace(/ /g, "-")
                let spellLink = 'https://app.roll20.net/compendium/dnd5e/Spells:'+encodeURIComponent(row.get('current'))+'#content'

                spellLink = makeButton(emojiLink, spellLink, 15)

                let spellInnate = getAttrByName(toChar.get('_id'),`${repeatingField}_innate`,'current');
                if (spellInnate.length > 2) { spellInnate = '<i>(' + spellInnate + ')</i>'; } else {spellInnate = ''; }

                // Build a spell description field here (Range, Action, Target, Duration, Con, Desc)
                // charDetail += repeatingAction + spellLink + row.get('current') + ', ';
                charDetail += repeatingAction + spellInnate + spellLink +  '</div>,';                  
              }
              charDetail = charDetail.slice(0,-2);
              charDetail += '</td></tr>';
            }
          });
          charDetail += '</table></td></tr></table>';

          // myDebug(4, charDetail);

          charMap.set(toObj[0].id, {id: toObj[0].id, charId: toChar.get('id'), txt: charDetail})

          charDetail_TOPage = openScrollCharBox + charDetail + closeScrollBox;
          charDetail_CSPage = openBox + charDetail + closeBox

          break;
      } // End of switch case for token type
    } // End test for using maps

    // Logic to expand or collapse the Character information
    if (state.DMDashboard.DetailExpand == 1) {
      toList = charHeader + btnCps + charDetail_TOPage;
    } else {
      toList = charHeader + btnExp;                    
    }
    charReport = openReport + charHeader_CSPage + charDetail_CSPage + closeReport;
    reportPerformance('Complete Character Detail');

    // Header row for the turnorder list
    toList += openScrollTOBox
    //toList += `<table style="border: 0px; padding: 0;background-color:#404040; border-collapse: collapse; margin: 0 auto;"><tr><td style="border: 0px; padding: 0;">Turn Order</td><td><span id=EncDiff>1</span></td></tr></table>`;
    toList +=  '<table ' + ts +'>';
    toList +=  '<thead><tr><th' + th + '>Turn</th>';
    toList +=  '<th' + th + '>Name</th>';
    // toList +=  '<th' + th + '>Foe?</th>';
    toList +=  '<th' + th + '>Functions</th>';
    toList +=  '<th' + th + '>HP</th>';
    toList +=  '<th' + th + '>Markers</th>';                
    toList +=  '<th' + th + '>AC</th>';
    toList +=  '<th' + th + '>PP</th>';
    toList +=  '<th' + th + '>Speed</th>';
    toList +=  '<th' + th + '>Senses/Saves</th>';
    toList +=  '<th' + th + '>Tootltip</th>';
    toList +=  '</tr></thead><tbody>';

    // **********************************************************
    // Build the table of items on the Turnorder list            
    // ***********************************************************

    // For each item in the Turnorder
    for (i=0;i<toObj.length;i++) {
      
      toList += '<tr style="border: 1px">';

      tType = getTokenType(toObj[i].id); // Returns NPC, CHAR, CUSTOM, UTILITY or OTHER
      isnpc = 0;
      tdbase = tdpc;

      switch (tType) {
        case 'OTHER':
          toToken = getObj("graphic", toObj[i].id);
          if (toToken){
            tknImg = `<img style = 'max-height: 40px; max-width: 40px; padding: 0px; margin: 0px !important' src = '${toToken.get('imgsrc')}'</img>`;

            // Col 1 (Turn/PR)
            toList += '<td ' + tdcustom + '><b>' + toObj[i].pr + '</b></td>';

            // Col 2 (Name/Img)
            tknImg = `<img style = 'max-height: 40px; max-width: 40px; padding: 0px; margin: 0px !important' src = '${toToken.get('imgsrc')}'</img>`;
            if(toToken.get('layer') == 'objects'){
              toList += '<td ' + tdcustom + '>' + makeButton(tknImg, `!DMDash --PingToken-GM ${toToken.get('_id')}`, 40) + '<b>' + toToken.get('name') + '</b></td>';
            } else {
              toList += '<td ' + tdcustom + '>' + makeButton(tknImg, `!DMDash --PingToken-GM ${toToken.get('_id')}`, 40) + '<i>' + toToken.get('name') + '</i></td>';
            }

            // Col 3 (Functions: Remove Item, Toggle Token between GM/Obj Layer)
            toList +=  '<td ' + tdcustom + '>' 
            toList += '<span style="font-size: 16px">'+ addTooltip("Remove Item from TurnOrder", makeButton(emojiClear,`!DMDash --TO-Remove ${toObj[i].id}`)) + '</span>';
            if(toToken.get('layer') == 'objects'){
              toList += '<span style="font-size: 16px">'+ addTooltip("Make Token Invisible", makeButton(emojiShow, '!DMDash --TokenToggleVisabity ' + toToken.get('_id')))+ '</span>';
             } else {
              toList += '<span style="font-size: 16px">'+ addTooltip("Make Token Visible", makeButton(emojiHide, '!DMDash --TokenToggleVisabity ' + toToken.get('_id')))+ '</span>'; 
            }

            if(toToken.get('lockMovement')){
              toList += '<span style="font-size: 16px">'+ addTooltip("Unlock Token Movement", makeButton(emojiLock, '!DMDash --TokenToggleLock ' + toToken.get('_id')))+ '</span>'; 
            } else {
              toList += '<span style="font-size: 16px">'+ addTooltip("Lock Token Movement", makeButton(emojiUnlock, '!DMDash --TokenToggleLock ' + toToken.get('_id')))+ '</span>'; 
            }
            if (toToken.get('showplayers_name')){
              toList += '<span style="font-size: 16px">'+  addTooltip("Hide Nameplate for Players", makeButton(emojiNameplate, '!DMDash --TokenToggleNameplate ' + toToken.get('_id')))+'</span>';
            } else {
              toList += '<span style="font-size: 16px">'+ addTooltip("Hide Nameplate for Players", makeButton(emojiNameplate, '!DMDash --TokenToggleNameplate ' + toToken.get('_id'))) + '</span>';
            }

            toList += '<span style="font-size: 16px">'+ addTooltip("Show Avatar", makeButton(emojiAvatar, '!DMDash --showAvatar ' + toToken.get('_id') + ' 1 1')) + '</span>'; 
            toList += '<span style="font-size: 16px">'+ addTooltip("Show Avatar", makeButton(emojiImages, '!DMDash --showImage ' + toToken.get('_id') + ' -1 1 1')) + '</span>';

            toList+='</td>'

            // Col 4 (Health)
            hp = toToken.get(`bar${state.DMDashboard.HPBar}_value`);
            hpmax = toToken.get(`bar${state.DMDashboard.HPBar}_max`);
            hp_pct = hp/hpmax * 100;
            hp_pct = hp_pct.toFixed(0);

            td_hp = td_health100;
            if (hp_pct < 75) {td_hp=td_health75;} // Yellow
            if (hp_pct < 50) {td_hp=td_health50;} // Orange
            if (hp_pct < 25) {td_hp=td_health25;} // Red 
            if (hp_pct <= 0) {td_hp=td_healthDead;} // Dead

            // Button to enable adjusting Hit Points
            btnAdjHP = makeButton(hp + ' / ' + hpmax,`!DMDash --TokenAdjustHP  ${toToken.get('_id')} ?{Adjust HP?|}`);
            toList += '<td ' + td_hp + '><c>'+ btnAdjHP + ' (' + hp_pct + '%)</c></td>';
            
            // COL 5 Status Markers (Span 5 (5-9))
            sm = toToken.get('statusmarkers');
            sm_Images = getSMImages(toToken.get('statusmarkers'));
            toList += '<td ' + tdbase + ' colspan=5>' + sm_Images + '</td>'; //StatusMarkers

            // Col 10 (Tooltips)
            tt = toToken.get('tooltip');
            toList += '<td ' + tdbase + '>' 
            toList += '<span style="font-size: 16px">'+ addTooltip("Ping Me - Show All Players", makeButton('\u{1F3AF}', `!DMDash --PingToken-All ${toToken.get('_id')}`))+ '</span>';
            toList += '<span style="font-size: 16px">'+ addTooltip("Edit Tooltip", makeButton(emojiEdit, `!DMDash --TokenSetTooltip ${toToken.get('_id')} "?{Edit Tooltip|${tt}}"`))+ '</span>' 
            // toList += '<span style="font-size: 16px">'+ addTooltip("Clear Tooltip", makeButton(emojiClear, '!DMDash --TokenClearTooltip ' + toToken.get('_id')))+ '</span>'

            if(toToken.get('show_tooltip')){
              toList += '<span style="font-size: 16px">'+ addTooltip("Hide Tooltip", makeButton(emojiHide, '!DMDash --TokenToggleTooltip ' + toToken.get('_id')))+ '</span>'
              toList += '<b>' + tt + '</b></td>';               
            } else {
              toList += '<span style="font-size: 16px">'+ addTooltip("Show Tooltip",makeButton(emojiShow, '!DMDash --TokenToggleTooltip ' + toToken.get('_id')))+ '</span>'
              toList += '<i>' + tt + '</i></td>'; 
            }
          }
          break;

        case 'CUSTOM':
          toList +=  '<td ' + tdcustom + '><b>' + toObj[i].pr + '</b></td>';
          toList +=  '<td ' + tdcustom + '>' + toObj[i].custom + ' [' + toObj[i].formula + ']' + '</td>';
          toList +=  '<td ' + tdcustom + '>' + '<span style="font-size: 16px">'+  addTooltip("Remove Item from Turnorder",makeButton('❌',`!DMDash --TO-RemoveCustom ${i}`,20)) + '</span></td>';
          toList +=  '<td ' + tdcustom + ' colspan=8></td>';
          break;
        case 'NPC':
        case 'NPC-CHARSHEET':
          isnpc = 1;
          tdbase = tdnpc;
          // NO break on purpose

        case 'CHAR':

          toToken = getObj("graphic", toObj[i].id);
          toChar = getObj('character', toToken.get('represents'));
          
          // Has this character already been loaded?
          if (foeMap.has(toObj[i].id)) {
            
            myDebug(2, `foe1: (foeItem Already Exists) ${toToken.get('name')}`)

            foeItem = foeMap.get(toObj[i].id);

            // Yes - Load up the appraproiate data from the foeMap
            if (foeItem.State == 'FRIEND'){
              //Friend
              myDebug(2, `foe2: (foeItem Exists and is Friend) ${toToken.get('name')}`)
              edPartyCount = edPartyCount + 1;
              edCharLevel = foeItem.Level;
              if (edCharLevel > 20) {edCharLevel = 20;}
              if (edCharLevel < 1) {edCharLevel = 1;}
              let xpThresholdItem = mapXPThresholds.get(edCharLevel)
              if (Object(xpThresholdItem.keys).length !== 0) {
                edEasy = Number(edEasy) + Number(xpThresholdItem.easy)
                edMedium = Number(edMedium) + Number(xpThresholdItem.medium)
                edHard = Number(edHard) + Number(xpThresholdItem.hard)
                edDeadly = Number(edDeadly) + Number(xpThresholdItem.deadly)
              }

            } else if (foeItem.State == 'FOE'){
              //Foe
              myDebug(2, `foe3: (foeItem Exists and is Foe) ${toToken.get('name')}`)              
              edFoeCount = edFoeCount + 1;
              edNPCExpTotal = edNPCExpTotal + foeItem.Exp;
            } else {
              myDebug(2, `foe4: (foeItem Exists and is Neutral) ${toToken.get('name')}`)              
            }

            
          } else {
            // No - Calculate data for the foeMap and load it
            if (tType == 'CHAR') {
              
              // Load CHAR into foe map, do the NPC Calcs now too
              myDebug(2, `foe5: (New Char to be added) ${toToken.get('name')}`)

              edPartyCount = edPartyCount + 1;
              edSpellCasterLvl = getAttrByName(toChar.get('_id'), 'caster_level');
              edCharLevel = getAttrByName(toChar.get('_id'),'level','current');
              if (edCharLevel > 20) {edCharLevel = 20;}
              if (edCharLevel < 1) {edCharLevel = 1;}
              let xpThresholdItem = mapXPThresholds.get(edCharLevel)
              if (Object(xpThresholdItem.keys).length !== 0) {
                edEasy = Number(edEasy) + Number(xpThresholdItem.easy)
                edMedium = Number(edMedium) + Number(xpThresholdItem.medium)
                edHard = Number(edHard) + Number(xpThresholdItem.hard)
                edDeadly = Number(edDeadly) + Number(xpThresholdItem.deadly)
                edCR = CharLvl_to_CR(edCharLevel, edSpellCasterLvl)
                edNPCExp = CR_to_XP(edCR)
                foeMap.set(toObj[i].id, {id: toObj[i].id, charId: toChar.get('id'), Type: 'CHAR', State:'FRIEND', Level: edCharLevel, SpellCasterLvl: edSpellCasterLvl, Exp: edNPCExp});
                foeItem = foeMap.get(toObj[i].id);
              }
              
            } else {
              
              //NPC
              myDebug(2, `foe6: (New NPC to be added) ${toToken.get('name')}`)
              edFoeCount = edFoeCount + 1;

              // This could be an NPC using a Charsheet - "controledby" Is blank
              if (getAttrByName(toChar.get('_id'), 'npc', 'current') == 0) {

                // It is a NPC in a Character's clothing (character sheet)
                myDebug(2, `foe7: (New NPC living in a Character Sheet (npc=0)) ${toToken.get('name')}`)
                edSpellCasterLvl = getAttrByName(toChar.get('_id'), 'caster_level')
                edCharLevel = getAttrByName(toChar.get('_id'),'level','current');
                edCR = CharLvl_to_CR(edCharLevel, edSpellCasterLvl)
                edNPCExp = CR_to_XP(edCR);
                edNPCExpTotal = edNPCExpTotal + edNPCExp;
                foeMap.set(toObj[i].id, {id: toObj[i].id, 
                                         charId: toChar.get('id'), 
                                         Type: 'NPC', 
                                         State:'FOE', 
                                         Level: edCharLevel, 
                                         SpellCasterLvl: edSpellCasterLvl,
                                         Exp: edNPCExp});
                foeItem = foeMap.get(toObj[i].id);

              } else {
                // It is a NPC using a traditional NPC setup
                myDebug(2, `foe8: (New Traditional NPC) ${toToken.get('name')}`)
                edNPCExp = getAttrByName(toChar.get('_id'),'npc_xp','current');
                edNPCExpTotal = edNPCExpTotal + edNPCExp;
                edSpellCasterLvl = getAttrByName(toChar.get('_id'), 'caster_level')
                edCharLevel = CR_to_CharLvl(getAttrByName(toChar.get('_id'), 'npc_challenge'), edSpellCasterLvl)
                foeMap.set(toObj[i].id, {id: toObj[i].id, 
                                         charId: toChar.get('id'), 
                                         Type: 'NPC', 
                                         State:'FOE', 
                                         Level: edCharLevel, 
                                         SpellCasterLvl: edSpellCasterLvl,
                                         Exp: edNPCExp});
                foeItem = foeMap.get(toObj[i].id);

              }
            }
          }

          // Col 1 (Turn/PR)
          toList += '<td ' + tdbase + '>'
          if (foeItem.State == 'FRIEND') {
            toList += '<span style="font-size: 16px">'+ addTooltip(`(Friend) ${foeItem.Type}/${foeItem.Level}/${foeItem.Exp}`, makeButton(emojiFriend, '!DMDash --ToggleFriend ' + toToken.get('_id'))) + '</span>';
          } else if (foeItem.State == 'FOE') {
            toList += '<span style="font-size: 16px">'+ addTooltip(`(Foe) ${foeItem.Type}/${foeItem.Level}/${foeItem.Exp}`, makeButton(emojiFoe, '!DMDash --ToggleFriend ' + toToken.get('_id'))) + '</span>';
          } else {
            toList += '<span style="font-size: 16px">'+ addTooltip(`(Neutral) ${foeItem.Type}/${foeItem.Level}/${foeItem.Exp}`, makeButton(emojiNeutral, '!DMDash --ToggleFriend ' + toToken.get('_id'))) + '</span>';
          }
          toList += '&nbsp;<b>' + toObj[i].pr + '</b></td>';


          // Col 2 (Name)
          tknImg = `<img style = 'max-height: 40px; max-width: 40px; padding: 0px; margin: 0px !important' src = '${toToken.get('imgsrc')}'</img>`;
          tknImg = addTooltip("Ping Me", makeButton(tknImg, `!DMDash --PingToken-GM ${toObj[i].id}`, 40)) 

          if(toToken.get('showplayers_name')){
            // toList += '<td ' + tdbase + '>' + makeButton(tknImg, '!ping-token --' + toToken.get('_id'), 40) + '<b>' + toToken.get('name') + '</b></td>';
            toList += '<td ' + tdbase + '>' + tknImg + '<b>' + addTooltip("Token Nameplate Visble",toToken.get('name')) + '</b></td>';                
          } else {
            // toList += '<td ' + tdbase + '>' + makeButton(tknImg, '!ping-token --' + toToken.get('_id'), 40) + '<i>' + toToken.get('name') + '</i></td>';
            toList += '<td ' + tdbase + '>' + tknImg + '<i>' + addTooltip("Token Nameplate Hidden",toToken.get('name')) + '</i></td>';                
          }

          // Col 3 (Commands)
          toList += '<td ' + tdbase + '>';
          //toList += '<span style="font-size: 16px;>' + addTooltip("Ping Me - Show All Players", makeButton(emojiPing, `!DMDash --PingToken-All ${toToken.get('_id')}`));
          toList += '<span style="font-size: 16px">'+ addTooltip("Ping Me - Show All Players", makeButton(emojiPing, `!DMDash --PingToken-All ${toToken.get('_id')}`)) + '</span>';
          toList += '<span style="font-size: 16px">'+ addTooltip("Remove Token from Turnorder", makeButton(emojiClear,`!DMDash --TO-Remove ${toObj[i].id}`)) + '</span>';

          if(toToken.get('layer') == 'objects'){
            toList += '<span style="font-size: 16px">'+ addTooltip("Hide Token on Map", makeButton(emojiShow, '!DMDash --TokenToggleVisabity ' + toToken.get('_id'))) + '</span>';  
          } else {
            toList += '<span style="font-size: 16px">'+ addTooltip("Unhide Token on Map", makeButton(emojiHide, '!DMDash --TokenToggleVisabity ' + toToken.get('_id'))) + '</span>';
          }

          toList +=  '<span style="font-size: 16px">'+ addTooltip("Open Character Sheet", makeButton(emojiDocument, 'https://journal.roll20.net/character/' + toToken.get('represents'))) + '</span>';
          // toList +=  '<span style="font-size: 16px">'+ addTooltip("Show GMNotes", makeButton(emojiNote, '!DMDash --showGMNote ' + toToken.get('_id'))) + '</span>';


          if (isnpc) {
            //tmp = toToken.get('gmnotes');
            toList += '<span style="font-size: 16px">'+ addTooltip('Add a Token Note', makeButton(emojiEdit, '!DMDash --AddTokenGMNote ' + toToken.get('_id') + ' "?{Token GM Note?|}"')) + '</span>';  
          } else {
            toList += '<span style="font-size: 16px">'+ addTooltip('Add a Character Note', makeButton(emojiEdit, '!DMDash --AddCharGMNote ' + toToken.get('represents') + ' "?{Character GM Note?|}"')) + '</span>';
          }

          //log('LockMovement' + toToken.get('lockMovement'))
          if(toToken.get('lockMovement')){
            toList += '<span style="font-size: 16px">'+ addTooltip("Unlock token Movement", makeButton(emojiLock, '!DMDash --TokenToggleLock ' + toToken.get('_id'))) + '</span>'; 
          } else {
            toList += '<span style="font-size: 16px">'+ addTooltip("Unlock token Movement", makeButton(emojiUnlock, '!DMDash --TokenToggleLock ' + toToken.get('_id'))) + '</span>'; 
          }

          if (toToken.get('showplayers_name')){
            //toList += addTooltip("Hide Nameplate for Players", makeButton('📛', '!DMDash --TokenNameplate ' + toToken.get('_id'))) ;
            toList += '<span style="font-size: 16px">'+ addTooltip("Hide Nameplate to Players", makeButton(emojiNameplate, '!DMDash --TokenToggleNameplate ' + toToken.get('_id'))) + '</span>';
          } else {
            toList += '<span style="font-size: 16px">'+ addTooltip("Show Nameplate to Players", makeButton(emojiNameplate, '!DMDash --TokenToggleNameplate ' + toToken.get('_id')))  + '</span>';
          }
          toList += '<span style="font-size: 16px">'+ addTooltip("Show Avatar", makeButton(emojiAvatar, '!DMDash --showAvatar ' + toToken.get('_id') + ' 1 1')) + '</span>'; 
          toList += '<span style="font-size: 16px">'+ addTooltip("Show Images", makeButton(emojiImages, '!DMDash --showImage ' + toToken.get('_id') + ' -1 1 1')) + '</span>';
          toList += '</td>';

          // Col 4 (Health)
          hp = toToken.get(`bar${state.DMDashboard.HPBar}_value`);
          hpmax = toToken.get(`bar${state.DMDashboard.HPBar}_max`);

          hp_pct = hp/hpmax * 100;
          hp_pct = hp_pct.toFixed(0);

          td_hp = td_health100;
          if (hp_pct < 75) {td_hp=td_health75;} // Yellow
          if (hp_pct < 50) {td_hp=td_health50;} // Orange
          if (hp_pct < 25) {td_hp=td_health25;} // Red 
          if (hp_pct <= 0) {td_hp=td_healthDead;} // Dead

          btnAdjHP = makeButton(hp + ' / ' + hpmax,`!DMDash --TokenAdjustHP  ${toToken.get('_id')} ?{Adjust HP?|}`);
          toList = toList + '<td ' + td_hp + '><c>'+ btnAdjHP + ' (' + hp_pct + '%)</c></td>';
          
          // COL 5 Status Markers
          sm = toToken.get('statusmarkers');
          sm_Images = getSMImages(toToken.get('statusmarkers'));

          toList += '<td ' + tdbase + '>' + sm_Images + '</td>'; //StatusMarkers

          // Col 6 (AC)
          if (isnpc) {
            toList += '<td ' + tdbase + '>🛡' + getAttrByName(toChar.get('_id'),'npc_ac','current') + '</td>'; //AC
          } else {
            toList += '<td ' + tdbase + '>🛡' + getAttrByName(toChar.get('_id'),'ac','current') + '</td>'; //AC
          }
          
          // Col 7 (Passive Perception)
          pp = getAttrByName(toChar.get('_id'),'passive_wisdom','current');                        
          toList += '<td ' + tdbase + '>👀' + pp + '</td>'; //PP

          // Get NPC specific data if this is an NPC
          if (isnpc) {
            // Yes - Get NPC based fields
            speed = getAttrByName(toChar.get('_id'),'npc_speed','current');
            senses = getAttrByName(toChar.get('_id'),'npc_senses','current');
          } else {
            speed = getAttrByName(toChar.get('_id'),'speed','current');
            if (toToken.get('has_night_vision')){
              senses ='darkvision ' + toToken.get('night_vision_distance') + ' ft.';
            } else {
              senses ='';
            }
          }

          // Col 8 (Speed)
          toList += '<td ' + tdbase + '>👣' + speed + '</td>'; //Speed

          // Col 9 (Senses)
          toList += '<td ' + tdbase + '><table style="border: 0px; padding: 0;background-color:#404040; border-collapse: collapse;"><tr style="border: 0px; padding: 0;><td ' + tdbase + '>' + senses + '</td>'; //Senses

          btnSaves = makeButton('Str', `!&#13;&#64;{${toChar.get('name')}|strength_save_roll}`) + ' '
          btnSaves += makeButton('Dex', `!&#13;&#64;{${toChar.get('name')}|dexterity_save_roll}`) + ' '
          btnSaves += makeButton('Con', `!&#13;&#64;{${toChar.get('name')}|constitution_save_roll}`) + ' '
          btnSaves += makeButton('Wis', `!&#13;&#64;{${toChar.get('name')}|wisdom_save_roll}`) + ' '
          btnSaves += makeButton('Int', `!&#13;&#64;{${toChar.get('name')}|intelligence_save_roll}`) + ' '
          btnSaves += makeButton('Cha', `!&#13;&#64;{${toChar.get('name')}|charisma_save_roll}`)

          toList += '<tr><td ' + tdbase + '>' +  btnSaves + '</td></tr></table></td>'; //Saves
          
          //toList += '<td ' + tdbase + '>' + senses + '</td>'; //Senses

          // Col 10 (Tooltips)
          tt = toToken.get('tooltip');
          toList += '<td ' + tdbase + '>';
          toList += '<span style="font-size: 16px">'+ addTooltip("Edit Tooltip", makeButton(emojiEdit, `!DMDash --TokenSetTooltip ${toToken.get('_id')} "?{Edit Tooltip|${tt}}"`)) +'</span>'
          // toList += '<span style="font-size: 16px">'+ addTooltip("Clear Tooltip", makeButton(emojiClear, '!DMDash --TokenClearTooltip ' + toToken.get('_id')) )+'</span>'

          if(toToken.get('show_tooltip')){
            toList += '<span style="font-size: 16px">'+ addTooltip("Hide Tooltip", makeButton(emojiShow, '!DMDash --TokenToggleTooltip ' + toToken.get('_id')))+'</span>'
            toList += '<b>' + tt + '</b></td>'; 
          } else {
            toList += '<span style="font-size: 16px">'+ addTooltip("Show Tooltip", makeButton(emojiHide, '!DMDash --TokenToggleTooltip ' + toToken.get('_id')))+'</span>'
            toList += '<i>' + tt + '</i></td>'; 
          }

          break;
      } // End switch (token type)

      toList += '</tr>';
    }

    toList += '</tbody></table>';
    toList += closeScrollBox;
    // lines = lines + toList;

    //************************************
    // Calculate the Encounter Difficulty
    //************************************
    if (edFoeCount == 0 || edPartyCount == 0){ //Nothing to do
      edDiffMult = 0;
    }
    if (edFoeCount == 1) {
      if (edPartyCount < 2) {
        edDiffMult = 1.5;
      } else if (edPartyCount > 5) {
        edDiffMult = .5;
      } else {
        edDiffMult = 1;
      }
    }

    if (edFoeCount == 2) {
      if (edPartyCount < 2) {
        edDiffMult = 2;
      } else if (edPartyCount > 5) {
        edDiffMult = 1;
      } else {
        edDiffMult = 1.5;
      }
    }

    if (edFoeCount >= 3 && edFoeCount <= 6) {
      if (edPartyCount < 2) {
        edDiffMult = 2.5;
      } else if (edPartyCount > 5) {
        edDiffMult = 1.5;
      } else {
        edDiffMult = 2;
      }
    }

    if (edFoeCount >= 3 && edFoeCount <= 6) {
      if (edPartyCount < 2) {
        edDiffMult = 2.5;
      } else if (edPartyCount > 5) {
        edDiffMult = 1.5;
      } else {
        edDiffMult = 2;
      }
    }

    if (edFoeCount >= 7 && edFoeCount <= 10) {
      if (edPartyCount < 2) {
        edDiffMult = 3;
      } else if (edPartyCount > 5) {
        edDiffMult = 2;
      } else {
        edDiffMult = 2.5;
      }
    }

    if (edFoeCount >= 11 && edFoeCount <= 14) {
      if (edPartyCount < 2) {
        edDiffMult = 4;
      } else if (edPartyCount > 5) {
        edDiffMult = 2.5;
      } else {
        edDiffMult = 3;
      }
    }

    if (edFoeCount >= 15) {
      if (edPartyCount < 2) {
        edDiffMult = 5;
      } else if (edPartyCount > 5) {
        edDiffMult = 3;
      } else {
        edDiffMult = 4;
      }
    }

    edEncounterExp = edNPCExpTotal * edDiffMult;
    if (edEncounterExp < edEasy) {
      edDifficulty = 'Trivial';
      edColor = '#ced9e0';
    }
    if (edEncounterExp > edEasy && edEncounterExp < edMedium) {
      edDifficulty = 'Easy';
      //edColor = '#24CE10';
      edColor = '#00c797';
    }
    if (edEncounterExp >= edMedium && edEncounterExp < edHard) {
      edDifficulty = 'Medium';
      //edColor = '#FFFE00';      
      edColor = '#f5a623';
    }
    if (edEncounterExp >= edHard && edEncounterExp < edDeadly) {      
      edDifficulty = 'Hard';
      //edColor = '#FFAC00';
      edColor = '#e45a1d';
    }
    if (edEncounterExp >= edDeadly) {      
      edDifficulty = 'Deadly';
      //edColor = '#FF0000';
      edColor = '#d54f4f';
    }

    let htmlTbl = [];
    htmlTbl.push(`<table style="border-collapse: collapse; padding: 0; border: 0; margin: 0 auto;">`)
    htmlTbl.push(`<tr><th style="background-color: ${edColor}; color: black;padding: 0px; border: 0px; text-align: center" colspan=8>Encounter ${edDifficulty} <i>(Total Encounter Experience: ${edEncounterExp})</i></th></tr>`)
    htmlTbl.push(`<tr><td style="padding: 0px; border: 0px; text-align: center">Easy</td>`)
    htmlTbl.push(`<td style="padding: 0px; border: 0px; text-align: center">Medium</td>`)
    htmlTbl.push(`<td style="padding: 0px; border: 0px; text-align: center">Hard</td>`)
    htmlTbl.push(`<td style="padding: 0px; border: 0px; text-align: center">Deadly</td>`)
    htmlTbl.push(`<td style="padding: 0px; border: 0px; text-align: center">Party #</td>`)
    htmlTbl.push(`<td style="padding: 0px; border: 0px; text-align: center">Foe #</td>`)
    htmlTbl.push(`<td style="padding: 0px; border: 0px; text-align: center">Mult</td>`)
    htmlTbl.push(`<td style="padding: 0px; border: 0px; text-align: center">Exp Total</td></tr>`)
    htmlTbl.push(`<tr><td style="padding: 0px; border: 0px; text-align: center">${edEasy}</td>`)
    htmlTbl.push(`<td style="padding: 0; border: 0; text-align: center">${edMedium}</td>`)
    htmlTbl.push(`<td style="padding: 0; border: 0; text-align: center">${edHard}</td>`) 
    htmlTbl.push(`<td style="padding: 0; border: 0; text-align: center">${edDeadly}</td>`)
    htmlTbl.push(`<td style="padding: 0; border: 0; text-align: center">${edPartyCount}</td>`)
    htmlTbl.push(`<td style="padding: 0; border: 0; text-align: center">${edFoeCount} </td>`)
    htmlTbl.push(`<td style="padding: 0; border: 0; text-align: center">${edDiffMult}</td>`)
    htmlTbl.push(`<td style="padding: 0; border: 0; text-align: center">${edNPCExpTotal}</td></tr></table>`) //Σ NPC Exp: 

    edTbl = htmlTbl.join('');

    //replaceDynamicSpanElement(toList,"encdiff",edDifficulty);
    toList = toList.replace('<span id=EncDiff>1</span>', edTbl);
    // log('Difficult: ' + edDifficulty);

    reportPerformance('Complete Turorder List');

    //*******************************************************************
    // Build a list of NPC and PC tokens that aren't on the TurnOrder list
    // with functionality to add them in groups (PC/NPC) or individually
    //*******************************************************************
    let btnNPCs = '';
    let btnPCs = '';
    let btnAddAllNPCs = '';
    let btnAddAllPCs = '';
    let toStr = Campaign().get("turnorder");

    // For every token on the page
    tokens.forEach(t => {
      tType = getTokenType(t.get('_id'));
      if (tType == 'NPC' || tType == 'CHAR'){
        let ndx = toStr.indexOf(t.get('_id'));  //If this TokenId isn't found in the current turnorder, add it to the list of available tokens?
        if(ndx<0){
          let tImg = `<img style = 'max-height: 40px; max-width: 40px; padding: 0px; margin: 0px !important' src = '${t.get('imgsrc')}'</img>`;
          let btn_ping = makeButton(tImg, `!DMDash --PingToken-GM ${t.id}`);
          if (tType == 'NPC') {
            btnNPCs += btn_ping + t.get('name') + makeButton(emojiPlus, `!DMDash --Initiative ${t.get('_id')}`);
            btnAddAllNPCs += t.get('_id') + ',';
          } else {
            btnPCs += btn_ping + t.get('name') + makeButton(emojiPlus, `!DMDash --Initiative ${t.get('_id')}`);
            btnAddAllPCs += t.get('_id') + ',';
          }
        }
      }
    });

    btnAddAllNPCs = btnAddAllNPCs.replace(/,([^,]*)$/, '$1'); // Remove last comma
    btnAddAllNPCs = '!DMDash --Initiative ' + btnAddAllNPCs;
    btnAddAllNPCs = makeButton(emojiPlus, btnAddAllNPCs ,20) + '  ';

    btnAddAllPCs = btnAddAllPCs.replace(/,([^,]*)$/, '$1');
    btnAddAllPCs = '!DMDash --Initiative ' + btnAddAllPCs;
    btnAddAllPCs = makeButton(emojiPlus, btnAddAllPCs ,20) + '  ';

    toList += '<b><u>NPCs</u></b>' + btnAddAllNPCs + '<br>' + btnNPCs + '<br><b><u>Player Characters</u></b>' + btnAddAllPCs + '<br>' + btnPCs + '<br>';
    toList = btns + openReport + toList + closeReport;

    // Footer links for the other handouts
    toList += "<div style='font-style:italic; color:#fff; margin-right:3px; padding:3px; text-align:right;'> <b>Other DM Tools:</b> "
    toList += makeButton('Character Sheet', 'https://journal.roll20.net/handout/' + getHandout(toReportName).get('_id')) + '  |  ';
    toList += makeButton('Notes', 'https://journal.roll20.net/handout/' + getHandout('DM Notes').get('_id'))  + '  |  '
    toList += makeButton('Jukebox', 'https://journal.roll20.net/handout/' + getHandout('DM Jukebox').get('_id')) + '</div>';

    reportPerformance('Complete Report Build');

    //Test area
    gEndTime = new Date().getTime();
    let runTime = gEndTime - gStartTime;
    toList += `Execution time: ${runTime.toFixed(2)} milliseconds (Version: ${state.DMDashboard.version})`

    // Write out the results back to a handout.
    if (toList) {
      addTextToHandout(toList, toReportName, 0)
    }

    if (charReport) {
      charReport += "<div style='font-style:italic; color:#fff; margin-right:3px; padding:3px; text-align:right;'>" + makeButton('Turnorder Dashboard', 'https://journal.roll20.net/handout/' + getHandout(toReportName).get('_id')) + '  |  ';
      // charReport += makeButton('Turnorder Log', 'https://journal.roll20.net/handout/' + getNoteLog().get('_id')) + '</div>';
      addTextToHandout(charReport, CharSheetName, 0)
    }
    reportPerformance('RefreshReport End');
    return;
  };

  function buildDMNotesHandout(){
    // Purpose: One stop repor to see all Notes/Tooltips/GMNotes/Bio for all tokens, characters and handouts
    // Context sensative 2-tier menu
    //  Tier 1: Tokens (current page) / Characters / Handouts / Filter Symbol (current filter)
    //   Tier 2: Token - Token Note / Character BIO / Character GMNote / Token Tooltip
    //   Tier 2: Character - Bio / Character / GMNote
    //   Tier 2: handout - Note / GMNote
    //     Note: Use bold/italic to indicate if a note/bio/gmnote/tooltip even exists for selected token/character/handout
    //  Simple Filter logic (Contains text), Use the HE function or some other means to clean it up
    // 3 column table ()
    //   Column 1: List of Tokens, Characters or Handouts that meet the Filter Criteria
    //     * Ability to select an item (emoji icons).  Differentiate selected item with different background color and font style
    //   Column 2: Functions (Ping Token, Open Character sheet, Open Handout)
    //   Column 3: Associated note based on selection of Tier 1, Tier 2 and listed item

    // Connect to the "DM Notes" handout - If it doesn't exist, create it.
    let rptText = ''; // This will be where the content of the new handout will be built
    let rptHeader = '';
    let rptFooter = '';
    let noteItem = '';
    let tblMaster = ''; // HTML containing Master table for Item List and associcated note
    let tblList = ''; // HTML table containg list of items and functions
    let tblNote = ''; // HTML table containg associated note
    let txtNote = '';
    let txtField = '';
    let imgField = '';
    let imgURL = '';

    let txtTokenBox = ''
    let imgTokenURL = ''
    let avatarURL = ''
    let fieldName1 = ''
    let fieldName2 = ''

    let btnFav = ''
    let btnFavsOn = ''
    let btnClearFavs = ''
    let btnsTier1 = '';
    let btnsTokenTier2 = '';
    let btnsCharTier2 = '';
    let btnsHOTier2 = '';
    let menuT1 = '';
    let menuT2 = '';
    
    const hoNotesName = 'DM Notes';
    const hoNotes = getHandout(hoNotesName);
        
    let tId = '';
    let cId = '';
    let hoId = '';

    let btnFct = ''

    let tokens = [];
    let characters = [];
    let handouts = [];
    let tObj = [];
    let cObj = [];
    let hoObj = [];
    let o = [];

    const txtBreadCrumb = `${state.DMDashboard.NotesRpt_Tier1MenuSelected} -> ${state.DMDashboard.NotesRpt_Tier2MenuSelected} -> ${state.DMDashboard.NotesRpt_SelectedId}`    
    rptHeader = html.h2('DM Notes Handout')    
    
    //-----Header------------------------------------------------ ;rptHeader
    //--Tier 1 menu --------------------------------------------- ;menuT1
    //----------------------------------------------------------- ;blank line?
    //[                 |                                       ] ;tblMaster   2-Column Master table
    //[Item List | Fcts]|[         Notes                        | ;   2-Column List Table/Fct & 1 column Note Table 
    //[                 |                                       ]
    //----Footer ------------------------------------------------

    myDebug(3, 'DM Notes Handout Build Started');
    myDebug(4, `T1:${state.DMDashboard.NotesRpt_Tier1MenuSelected} Id:${state.DMDashboard.NotesRpt_SelectedId}`)

    // Todo - Add logic to track selections and change button look for selected items

    // Tier 1 Buttons
    let btnTokens = makeMenuButton('Tokens', '!DMNotes --Tokens');  // Token Selected from Tier 1 menu
    let btnChars = makeMenuButton('Characters', '!DMNotes --Chars'); // Character Selected from tier 1 menu
    let btnHandouts = makeMenuButton('Handouts', '!DMNotes --Handouts');  // Handout Selected from Tier 1 menu
    btnFavsOn = makeMenuButton('Favorites', '!DMNotes --FavsOn 1');  // Show All
    if (state.DMDashboard.NotesRpt_FavsOn == 1) {
      btnFavsOn = makeMenuButton('Favorites', '!DMNotes --FavsOn 0', 1);  // Favorites Selected
    }
    btnClearFavs = makeMenuButton('Clear', '!DMNotes --ClearFav');

    let btnFilter = makeButton(emojiFilter + ' Filter', '!DMNotes --Filter ?{Where "Name" contains?|} ');  // Filter Selected from Tier 1 menu - Find Filter Emoji 

    let txtFilter = '(' + state.DMDashboard.NotesRpt_Filter + ')'
    // Tier 2 Buttons

    // Build the left list of objects
    switch(state.DMDashboard.NotesRpt_Tier1MenuSelected){
      case ('Tokens'):
        btnTokens = makeMenuButton('Tokens', '!DMNotes --Tokens', 1); 
        // Build list of tokens (Filtered at some point)
        tokens = findObjs({
          _type: 'graphic',
          _subtype: 'token',
          // controlledby: '',
          _pageid: Campaign().get('playerpageid')
        }).sort((a, b) => (a.get("name") > b.get("name") ? 1 : -1));

        tblList = '';

        tokens.forEach(tObj => {
          if (tObj.get('layer') == 'objects' || tObj.get('layer') == 'gmlayer') {          
            if (state.DMDashboard.NotesRpt_Filter.length == 0 || (tObj.get('name') && tObj.get('name').toLowerCase().includes(state.DMDashboard.NotesRpt_Filter.toLowerCase()))) {
              if (tObj.get('name') != undefined && tObj.get('name').length != 0) {

                // If this objects id is in the noteFavsMap, then it is a favorite and nees some love
                bFilterRow = false;
                if (state.DMDashboard.NotesRpt_FavsAry.includes(tObj.get('_id'))){
                  btnFav = makeButton(emojiFav, `!DMNotes --togglefav ${tObj.get('_id')}`)
                } else {
                  btnFav = makeButton(emojiNotFav, `!DMNotes --togglefav ${tObj.get('_id')}`)
                  if (state.DMDashboard.NotesRpt_FavsOn == 1) {
                    bFilterRow = true;
                  }
                }
                if (!bFilterRow){
                  btnFct = addTooltip("Ping Me - GM Only", makeButton(emojiPing, `!DMDash --PingToken-GM ${tObj.get('_id')}`));
                  btnItem = makeButton(tObj.get('name'), `!DMNotes --rowselected ${tObj.get('_id')}` )
                  if (state.DMDashboard.NotesRpt_SelectedId == tObj.get('_id')){
                    tblList += html.tr(html.td(btnFav, {'Width': '10%'}) +  html.td(btnItem, {'Width': '80%', 'background-color':'yellow'}) + html.td(btnFct, {'Width': '10%'}))
                  } else {
                    tblList += html.tr(html.td(btnFav, {'Width': '10%'}) +  html.td(btnItem, {'Width': '80%'}) + html.td(btnFct, {'Width': '10%'}))
                  }
                }
              }
            }
          }
        })
        tblList = html.table(tblList)
        break;
      case ('Characters'):
        // Build list of characters
        btnChars = makeMenuButton('Characters', '!DMNotes --Chars', 1); // Character Selected from tier 1 menu
        characters = findObjs({
          _type: 'character'
        }).sort((a, b) => (a.get("name") > b.get("name") ? 1 : -1));

        tblList = '';
        characters.forEach(cObj => {
          if (state.DMDashboard.NotesRpt_Filter.length == 0 || (cObj.get('name') && cObj.get('name').toLowerCase().includes(state.DMDashboard.NotesRpt_Filter.toLowerCase()))) {          
            btnFct = addTooltip("Open Character Sheet", makeButton(emojiDocument, `https://journal.roll20.net/character/${cObj.get('_id')}`));
            btnItem = makeButton(cObj.get('name'), `!DMNotes --rowselected ${cObj.get('_id')}`)

            // If this objects id is in the noteFavsMap, then it is a favorite and nees some love
            bFilterRow = false;
            if (state.DMDashboard.NotesRpt_FavsAry.includes(cObj.get('_id'))){
              btnFav = makeButton(emojiFav, `!DMNotes --togglefav ${cObj.get('_id')}`)
            } else {
              btnFav = makeButton(emojiNotFav, `!DMNotes --togglefav ${cObj.get('_id')}`)
              if (state.DMDashboard.NotesRpt_FavsOn == 1) {
                bFilterRow = true;
              }
            }

            if (!bFilterRow){
              if (state.DMDashboard.NotesRpt_SelectedId == cObj.get('_id')){
                myDebug(4, 'Selected Character:' + cObj.get('name'))
                tblList += html.tr(html.td(btnFav, {'Width': '10%'}) +  html.td(btnItem, {'Width': '80%', 'background-color':'yellow'}) + html.td(btnFct, {'Width': '10%'}))
              } else {
                tblList += html.tr(html.td(btnFav, {'Width':'10%}'}) + html.td(btnItem, {'Width': '80%'}) + html.td(btnFct, {'Width': '10%'}))
              }
            } 
          }
        })
        tblList = html.table(tblList)
        break;
      case ('Handouts'):
        btnHandouts = makeMenuButton('Handouts', '!DMNotes --Handouts', 1);  // Handout Selected from Tier 1 menu
        // Build list of handouts
        handouts = findObjs({
          _type: 'handout'
        }).sort((a, b) => (a.get("name") > b.get("name") ? 1 : -1));

        tblList = '';
        handouts.forEach(hoObj => {


          // If this objects id is in the noteFavsMap, then it is a favorite and nees some love
          bFilterRow = false;
          if (state.DMDashboard.NotesRpt_FavsAry.includes(hoObj.get('_id'))){
            btnFav = makeButton(emojiFav, `!DMNotes --togglefav ${hoObj.get('_id')}`)
          } else {
            btnFav = makeButton(emojiNotFav, `!DMNotes --togglefav ${hoObj.get('_id')}`)
            if (state.DMDashboard.NotesRpt_FavsOn == 1) {
              bFilterRow = true;
            }
          }

          if (!bFilterRow){
            if (state.DMDashboard.NotesRpt_Filter.length == 0 || (hoObj.get('name') && hoObj.get('name').toLowerCase().includes(state.DMDashboard.NotesRpt_Filter.toLowerCase()))) {
              btnFct = addTooltip("Open Handout", makeButton(emojiDocument, `https://journal.roll20.net/handout/${hoObj.get('_id')}`));
              btnItem = makeButton(hoObj.get('name'), `!DMNotes --rowselected ${hoObj.get('_id')}`)
              if (state.DMDashboard.NotesRpt_SelectedId == hoObj.get('_id')){
                myDebug(4, 'Selected Handout:' + hoObj.get('name'))
                tblList += html.tr(html.td(btnFav, {'Width': '10%'}) +  html.td(btnItem, {'Width': '80%', 'background-color':'yellow'}) + html.td(btnFct, {'Width': '10%'}))
              } else {
                tblList += html.tr(html.td(btnFav, {'Width': '10%'}) +  html.td(btnItem, {'Width': '80%'}) + html.td(btnFct, {'Width': '10%'}))
              }
            }
          }
        })
        tblList = html.table(tblList)
        break;
    }

    menuT1 = `${btnTokens} ${btnChars} ${btnHandouts}   |   ${btnFavsOn} ${btnClearFavs}   |   ${btnFilter} ${txtFilter}`
    menuT1 = html.div(menuT1, menuBoxCSS)
    tblList = openScrollBox + tblList + closeScrollBox;
    txtNote = 'Empty';

    if (state.DMDashboard.NotesRpt_SelectedId === '') {
      txtnote = 'No Item Selected';
      tblNote = html.div(txtnote)
      masterTable = html.table(html.tr(html.td(tblList, {'Width': '20%'}) + html.td(tblNote, {'Width': '80%'})))


      gEndTime = new Date().getTime();
      let runTime = gEndTime - gStartTime;
      rptFooter = html.p(`Execution time: ${runTime.toFixed(2)}ms. Selected Obj Id: N/A (Version: ${state.DMDashboard.version})`)
            
      rptText = openReport + rptHeader + menuT1 + masterTable + rptFooter + closeReport;
      if (rptText) {
        addTextToHandout(rptText, hoNotesName, 0)
      }
      return;
    } else {
      txtField = '';
      switch (state.DMDashboard.NotesRpt_Tier1MenuSelected) {
        case('Tokens'):
          o = getObj('graphic', state.DMDashboard.NotesRpt_SelectedId);
          if (o) {
            if (o.get('represents') != ''){
              o = getObj('character', o.get('represents'));
            }
            objectName = 'Character';
            fieldName1 = 'bio'  // Character Bio
            fieldName2 = 'gmnotes' // Character GM Notes
          }
          break;
        case('Characters'):
          o = getObj('character', state.DMDashboard.NotesRpt_SelectedId);
          objectName = 'Character';
          fieldName1 = 'bio'
          fieldName2 = 'gmnotes'

          break;
        case('Handouts'):
          o = getObj('handout', state.DMDashboard.NotesRpt_SelectedId);
          objectName = 'Handout';
          fieldName1 = 'notes'
          fieldName2 = 'gmnotes'
          break;
      }
    }

    if (!o) {
      myDebug(3, "No Object Obtained")
      rptText = html.tr(html.td(tblList, {'Width': '25%'}) + html.td('', {'Width': '75%'}))
      rptText = html.table(rptText)
      rptText = openReport + rptHeader + menuT1 + rptText + rptFooter + closeReport
      addTextToHandout(rptText, hoNotesName, 0);
    } else {
      myDebug(3, `Getting ${fieldName1} and ${fieldName2} for ${o.get('name')}`)

      o.get(fieldName1, function(notes1) {
        o.get(fieldName2, function(notes2) {

          let txtTokenBox = '';

          if (state.DMDashboard.NotesRpt_Tier1MenuSelected == 'Tokens') {
            // Get Token Tooltip, gmnotes and imgsrc
            tObj = getObj('graphic', state.DMDashboard.NotesRpt_SelectedId);
            if (tObj) {
              imgTokenURL = tObj.get('imgsrc');
              if (imgTokenURL != '') {
                imgTokenURL = html.img('', img1CSS, imgTokenURL, '', "50%", "auto" )
              }
              let txtTooltip = html.h3('Token Tooltip') + html.div(decodeHtmlString(tObj.get('tooltip')),shadoweddivCSS);
              let txtGMNotes =  html.h3('Token GM Notes') + html.div(decodeHtmlString(tObj.get('gmnotes')),shadoweddivCSS);
              txtTokenBox += html.div(txtTooltip + txtGMNotes, boundingdivCSS)
            }
          }

          let imgBox = ''
          // Get avatar associated with this character/handout
          avatarURL = o.get('avatar')
          if (avatarURL != '') {
            //avatarURL = html.div(imgBorderDS(avatarURL)) //DIV Container 
            avatarURL = html.img('', img1CSS, avatarURL, '', "50%", "auto");
          }

          if (avatarURL != '' && imgTokenURL != '') {
            imgBox = html.tr(html.td(html.h3('Token Image'), {'width' : '50%'}) + html.td(html.h3(objectName + ' Avatar/Image'), {'width' : '50%'}))
            imgBox += html.tr(html.td(imgTokenURL, {'text-align':'center', 'width' : '50%'}) + html.td(avatarURL, {'text-align':'center','width' : '50%'}))
            imgBox = html.table(imgBox);
                        
          } else if (imgTokenURL != '' && avatarURL == '') {
            imgBox = html.table(html.tr(html.td(html.h3('Token Image') + imgTokenURL)), {'text-align':'center', 'width' : '100%'})
          } else if (avatarURL != '' && imgTokenURL == '') {
            imgBox = html.table(html.tr(html.td(html.h3(' Avatar/Image') + avatarURL)), {'text-align':'center', 'width' : '100%'})
          }
    
          // Get Notes/Bio and GM Notes field data from Character or Handout object 
          notes1 = decodeHtmlString(notes1)
          notes2 = decodeHtmlString(notes2)
          let txtNote1 = html.div(html.h3(objectName + ' Notes/Bio') + html.div(notes1, shadoweddivCSS), boundingdivCSS)
          let txtNote2 = html.div(html.h3(objectName + ' GM Notes') + html.div(notes2, shadoweddivCSS), boundingdivCSS)
          let txtNoteBox = txtNote1 + txtNote2

          txtNoteBox = html.div(imgBox + txtTokenBox + txtNoteBox, divScrollBoxCSS) 
          rptText = html.tr(html.td(tblList, {'Width': '25%'}) + html.td(txtNoteBox, {'Width': '75%'}))
          rptText = html.table(rptText)

          gEndTime = new Date().getTime();
          let runTime = gEndTime - gStartTime;
          rptFooter = html.p(`Execution time: ${runTime.toFixed(2)} milliseconds (Version: ${state.DMDashboard.version})`)

          rptText = openReport + rptHeader + menuT1 + rptText + rptFooter + closeReport
          addTextToHandout(rptText, hoNotesName, 0);

        })
      })
    }
    myDebug(3, 'DM Notes Handout Build Ended');
    reportPerformance();
  }

  function listTracks() {
    let track = [];
    let tList = ''
    let tFavs = ''
    let rptText = ''; // This will be where the content of the new handout will be built
    let rptHeader = '';
    let rptFooter = '';
    let btnFavs = ''
    let btnAll = ''
    let btnLoop =''
    let btnDash = '';
    let btnPlay = '';
    let btnVol = ''
    let btnEffect = '';
    let btnAmbiance = '';

    let menuT1 = '';

    rptHeader = html.h2('DM Jukebox Tracks Management')    

    // Tier 1 Buttons
    if (state.DMDashboard.JB_Tier1MenuSelected == 'Jukebox') {
      btnAll = makeMenuButton('Jukebox', '!DMDash --JBAll', 1);  // All tracks
      btnFavs = makeMenuButton('Favorites', '!DMDash --JBFavs'); // Favorites 
    } else {
      btnAll = makeMenuButton('Jukebox', '!DMDash --JBAll');  // All tracks
      btnFavs = makeMenuButton('Favorites', '!DMDash --JBFavs', 1); // Favorites 
    }

    menuT1 = `${btnFavs} ${btnAll}`
    menuT1 = html.div(menuT1, menuBoxCSS)

    let tracks = findObjs({
      _type: 'jukeboxtrack'
    }).sort((a, b) => (a.get("title") > b.get("title") ? 1 : -1));

    if (tracks.length == 0) {
      myDebug(4, "No Tracks Found")
      return;
    }

    if (state.DMDashboard.JB_Tier1MenuSelected !== 'Jukebox') {
      // let tListEffects = html.tr(html.th('Play') + html.th('Loop') + html.th('Title') + html.th('Volume') )
      // let tListAmbiance = html.tr(html.th('Play') + html.th('Loop') + html.th('Title') + html.th('Volume') )

      let tListAmbiance =''
      let tListEffects =''
      let tRowAmbiance = ''
      let tRowEffects = ''
      let effectCols = 4;
      let ambCols = 2;
      let effColCnt = 0
      let ambColCnt = 0

      // It may be quicker to let the JB_AmbianceFavs and JB_ Effects 

      tracks.forEach(track => {
        
        let trackId = track.get('_id');
        btnPlay = makeButton(track.get('title'), `!dmdash --PlayTrack ${trackId}`);
        if (state.DMDashboard.JB_AmbianceFavs.includes(trackId)){
          ambColCnt++;
          if (ambColCnt ==  ambCols){
            ambColCnt = 0
            tRowAmbiance += html.td(btnPlay)
            tListAmbiance += html.tr(tRowAmbiance)
            tRowAmbiance = ''
          } else {
            tRowAmbiance += html.td(btnPlay)
          }
        }

        if (state.DMDashboard.JB_EffectsFavs.includes(trackId)){
          effColCnt++;
          if (effColCnt ==  effectCols){
            effColCnt = 0
            tRowEffects += html.td(btnPlay)
            tListEffects += html.tr(tRowEffects)
            tRowEffects = ''
          } else {
            tRowEffects += html.td(btnPlay)
          }
        }

      });
      tListEffects += html.tr(tRowEffects)
      tListEffects = html.table(tListEffects)
      tListAmbiance += html.tr(tRowAmbiance)
      tListAmbiance = html.table(tListAmbiance)
      tList = html.table(html.tr(html.th('Effect') + html.th('Ambiance')) +
              html.tr(html.td(tListEffects) + html.td(tListAmbiance)))

    } else {

      //tList = html.tr(html.th('Effect') + html.th('Ambiance') + html.th('Dash') + html.th('Play') + html.th('Title') + html.th('Playing') + html.th('Softstop') + html.th('Volume') + html.th('Loop'))
      tList = html.tr(html.th('Effect') + html.th('Ambiance') + html.th('Dash') + html.th('Play') + html.th('Loop') + html.th('Title') + html.th('Volume') )

      tracks.forEach(track => {

        let trackId = track.get('_id');
        btnPlay = makeButton(emojiPlay, `!dmdash --PlayTrack ${trackId}`);
        let dashOptions = 'Hit|Miss|Arrow|Door|Trap|Ouch|Boom|Pew|Magic|Steps|Amb1|Amb2'

        btnDash = makeButton(emojiEmptyBox, `!dmdash --SetDashTrack ${trackId} "?{Set Dashboard Track?|${dashOptions}}"`);
        const dbtExists = state.DMDashboard.JB_DashboardBtns.some(dbTrack => dbTrack.id === trackId);
        if (dbtExists == true) {
          let dbItem = state.DMDashboard.JB_DashboardBtns.find(dbTrack => dbTrack.id === trackId);
          if (dbItem) {
            btnDash = makeButton(dbItem.item, `!dmdash --SetDashTrack ${trackId} "?{Set Dashboard Track?|${dashOptions}}"`);    
          }
        }
        if (state.DMDashboard.JB_AmbianceFavs.includes(trackId)){
          btnAmbiance = makeButton(emojiAmbiance, `!dmdash --ToggleAmbianceTrack ${trackId}`)
        } else {
          btnAmbiance = makeButton(emojiEmptyBox, `!dmdash --ToggleAmbianceTrack ${trackId}`)
        }

        if (state.DMDashboard.JB_EffectsFavs.includes(trackId)){
          btnEffect = makeButton(emojiEffect, `!dmdash --ToggleEffectTrack ${trackId}`)
        } else {
          btnEffect = makeButton(emojiEmptyBox, `!dmdash --ToggleEffectTrack ${trackId}`)
        }

        if (track.get('loop') == true) {
          btnLoop = makeButton(emojiLoop, `!dmdash --LoopTrack ${trackId} 0`);
        }else{
          btnLoop = makeButton(emojiEmptyBox, `!dmdash --LoopTrack ${trackId} 1`);
        }
        btnVol = makeButton(emojiVolumeUp10, `!dmdash --SetVolume ${trackId} 10`) + makeButton(emojiVolumeUp1, `!dmdash --SetVolume ${trackId} 1`) + makeButton(emojiVolumeDown1, `!dmdash --SetVolume ${trackId} -1`)+makeButton(emojiVolumeDown10, `!dmdash --SetVolume ${trackId} -10`);
        tList += html.tr(html.td(btnEffect) + html.td(btnAmbiance) + html.td(btnDash) + html.td(btnPlay) + html.td(btnLoop) + html.td(track.get('title')) + html.td(btnVol + '[' + track.get('volume') + ']'))
      });
      tList = html.table(tList);
    }
    
    rptText = openReport + rptHeader + menuT1 + tList + rptFooter + closeReport

    addTextToHandout(rptText, "DM Jukebox",0)
    
  }

  function setDashTrack(id, selected){
    // First see if something is already assigned to the selected track
    const SelectedTrackExists = state.DMDashboard.JB_DashboardBtns.some(track => track.item === selected);
    // If it exists, then remove it
    if (SelectedTrackExists == true) {
      state.DMDashboard.JB_DashboardBtns = state.DMDashboard.JB_DashboardBtns.filter(track => track.item!==selected);
    }
    // Second see if something is already assigned to selected ID
    const SelectedIdExists = state.DMDashboard.JB_DashboardBtns.some(track => track.id === id);
    if (SelectedIdExists == true) {
      state.DMDashboard.JB_DashboardBtns = state.DMDashboard.JB_DashboardBtns.filter(track => track.id!==id);
    }

    // Add the new track
    state.DMDashboard.JB_DashboardBtns.push({
      item: selected,
      id: id
    });
    
  }

  function setLoopTrack(id, loop){
    let track = getObj('jukeboxtrack', id);
    if (!track) {
      myDebug(4, `Loop Track ${id} not found`);
      return;
    }
    if (loop == 0) {
      track.set('loop', false);  
    } else {
      track.set('loop', true);
    }
    return;
  }

  function setVolume(id, volAdj){
    let track = getObj('jukeboxtrack', id);
    if (!track) {
      myDebug(4, `SetVolume ${id} not found`);
      return;
    }
    let currVolume = track.get('volume');
    if (isNaN(currVolume)) {
      currVolume = 30;
    } else {
      currVolume = eval(Number(currVolume) + Number(volAdj));
    }
    if (currVolume > 100) { currVolume = 100; }
    if (currVolume < 0) { currVolume = 0; }
    track.set('volume', currVolume);
    return;
  }

  function playTrack(id){
    let track = getObj('jukeboxtrack', id);
    if (!track) {
      myDebug(4, `Playtrack ${id} not found`);
      return;
    }
    if (track.get('playing') == true) {
      // track.set('softstop', true);
      track.set('playing', false);
    } else {
      track.set('softstop', false);
      track.set('playing', true);
    }
    return;
  }

  function getPlayerAccess() {
    let playerAry = []

    let emojiAccess = '\u{2705}' //✅
    let emojiDenied = '\u{1F6AB}' //🚫
    let btnFct  = '';
    let access = '';
    let rowAccessCnt = 0;
    let tblRow = '';
    let tblDebug = ''

    // Initialize an empty string to store the information
    let output = '';
    let tblAccess = ''
    let playerCount = 0;

    let cb=''
    let ipj=''

    // Get all players
    const players = findObjs({ type: "player" }).sort((a, b) => (a.get("_displayname") > b.get("_displayname") ? 1 : -1));
  

    // Add the player's name to the content string
    output = html.h2(`Player Access Report`);
    output += html.p(`<i>&nbsp;&nbsp;${emojiNote}=In Player's Journal     ${emojiAccess}=Controlled By</i>`);
    tblAccess = html.th('Object Name')
    tblDebug = html.th('PlayerIds')

    // Iterate through all players
    players.forEach(player => {
      // Get the player's name
      let playerName = player.get("displayname");
      let playerId = player.get("_id");
      if (!playerIsGM(playerId)) {
        playerAry.push({name: playerName, id: playerId});
        tblAccess += html.th(playerName)
        tblDebug += html.td(playerId, {'font-size' : '9px'})
        playerCount++;
      }
    })

    access = '';
    tblAccess = html.tr(tblAccess);
    // tblAccess += html.tr(tblDebug);
    tblAccess += html.tr(html.td("<h3>Handouts</h3>"))
    let handouts = findObjs({ type: "handout" }).sort((a, b) => (a.get("name") > b.get("name") ? 1 : -1));
    handouts.forEach(handout => {
      access = '';
      btnFct = addTooltip("Open Handout", makeButton(emojiDocument, `https://journal.roll20.net/handout/${handout.get('_id')}`));
      let tblRow = html.td("&nbsp;&nbsp;" + btnFct + handout.get("name"))  // Add link to handout later
      playerAry.forEach(player => {
        ipj = handout.get("inplayerjournals");
        if(ipj.includes(player.id) || ipj === "all"){
          access += emojiNote
          rowAccessCnt++;
        }
        
        cb = handout.get("controlledby");
        if (cb.includes(player.id) || cb === "all") {
          access += emojiAccess
          rowAccessCnt++;
        }
        tblRow += html.td(access, {'text-align': 'center'})
        access = '';
      });
      if (rowAccessCnt > 0) {
        tblAccess += html.tr(tblRow);
      }
      rowAccessCnt = 0;
    })

    tblRow = html.td("<h3>Charactes</h3>")
    playerAry.forEach(player => {
      // Get the player's name
        tblRow += html.td(`<b>${player.name} </b>`)  // Add link to handout later
    });
    tblAccess += html.tr(tblRow);

    let characters = findObjs({ type: "character" }).sort((a, b) => (a.get("name") > b.get("name") ? 1 : -1));
    characters.forEach(character => {
      access = '';

      btnFct = addTooltip("Open Character Sheet", makeButton(emojiDocument, `https://journal.roll20.net/character/${character.get('_id')}`));
      tblRow = html.td("&nbsp;&nbsp;" + btnFct + character.get("name") )  // Add link to handout later
      playerAry.forEach(player => {
        ipj = character.get("inplayerjournals");
        if(ipj.includes(player.id) || ipj === "all"){
          access += emojiNote
          rowAccessCnt++;
        }
        
        cb = character.get("controlledby");
        if (cb.includes(player.id) || cb === "all") {
          access += emojiAccess
          rowAccessCnt++;
        }
        tblRow += html.td(access, {'text-align': 'center'})
        access = '';
      });
      if (rowAccessCnt > 0) {
        tblAccess += html.tr(tblRow);
      }
      rowAccessCnt = 0;
    })

    tblRow = html.td("<h3>Tokens</h3>")
    playerAry.forEach(player => {
      // Get the player's name
        tblRow += html.td(`<b>${player.name} </b>`)  // Add link to handout later
    });
    tblAccess += html.tr(tblRow);

    let pages = findObjs({ type: "page" }).sort((a, b) => (a.get("name") > b.get("name") ? 1 : -1));
    pages.forEach(page => {
      let graphics = findObjs({type: "graphic", _pageid: page.get("_id")}).sort((a, b) => (a.get("name") > b.get("name") ? 1 : -1));
      graphics.forEach(graphic => {
        tblRow = html.td("&nbsp;&nbsp;" + page.get("name") + ": " + graphic.get("name"))  // Add link to handout later

        if (graphic.get("represents")!== undefined && graphic.get("represents").length > 0){
          character = getObj("character", graphic.get("represents"));
          if (character) {
            cb = character.get("controlledby");
          } else {
            cb = 'N/A'
          }
        } else {
          cb = graphic.get("controlledby");
        }

        playerAry.forEach(player => {
          if (cb.includes(player.id) || cb === "all") {
            access += emojiAccess
            rowAccessCnt++;
          }
          tblRow += html.td(access, {'text-align': 'center'})
          access = '';
        });
        if (rowAccessCnt > 0) {
          tblAccess += html.tr(tblRow);
        }
        rowAccessCnt = 0;        
      });
    });

    tblAccess = html.table(tblAccess);

    output += tblAccess + html.p('<i><b>Note: </b>Objects not listed in the table above are only "controlled by" and "in the journal" of the GM.</i>');
    output = openReport + output + closeReport;
  
    // Check if a "Player Access" handout exists, or create one
    let playerAccessHandout = findObjs({ type: "handout", name: "DM Player Access" })[0];
    if (!playerAccessHandout) {
      playerAccessHandout = createObj("handout", { name: "DM Player Access" });
    }
  
    // Update the "Player Access" handout content
    playerAccessHandout.set("notes", output);
  
    // Notify the GM
    let chatMsg = `Player Access Handout has been updated.  Click the link bellow to view:`;      
    chatMsg += `<br>&nbsp;<b>[DM Player Access](https://journal.roll20.net/handout/${getHandout('DM Player Access').get('_id')})</b>`;      

    // sendChat("DM Dashboard", chatMsg);
    mySendChat(true, "Player Access", chatMsg)

  }

  startPeformanceCheck();

  // Load them at the start to improve performance in the 
  // HTML Codes
  const htmlCR = '&#13;'
  const htmlRBrace = '&#125;'
  const htmlLBrace = '&#123;'
  const htmlPct = '&#37;'
  const htmlAt = '&#64;'
  
  const tokenMarkers = JSON.parse(Campaign().get("token_markers"));
  const openReport = "<div style='color: #000; border: 1px solid #000; background-color: #EFEBD6; box-shadow: 0 0 3px #000; display: block; text-align: left; font-size: 13px; padding: 5px; margin-bottom: 2px; font-family: sans-serif; white-space: pre-wrap;'>";
  const openReportx = `<div style='background-image: linear-gradient(rgba(255, 255, 255, 0.5), rgba(255, 255, 255, 0.5)), url(https://i.imgur.com/8Mm94QY.png); background-size: 100% 100%; box-shadow: 0 0 3px #fff; display: block; text-align: left; font-size: 13px; padding: 5px; margin-bottom: 2px; color: black; font-family: serif; white-space: pre-wrap; line-height:1.2em; font-style:normal'>`;
  const closeReport = '</div>';
  const openHeader = "<div style='font-weight:bold; color:#fff; background-color:#404040; margin-right:3px; padding:3px;'>"
  const closeHeader = `</div>`;
  const openHeaderInfo = "<div style='font-style:italic; color:#fff; background-color:#404040; margin-right:3px; padding:3px; text-align:right;'>"

  const openChat = `<div style="border-radius: 10px ; border: none ; background-color: ; overflow: hidden ; width: 100%"><div style = "border-radius: 10px ; border: 2px solid #000000 ; background-color:  #00000000; overflow: hidden ; margin: 0px 16px 16px 0px ; box-shadow: 5px 8px 8px #888888">`
  const closeChat= `<\div><\div>`;
  const tblChatStyle = `width: 100% ; margin: 0 auto ; border-collapse: collapse ; font-size: 12px;`
  const trhChatStyle = `border-bottom: 1px solid #000000 ; font-weight: bold ; line-height: 22px ; background-color: #521e10 ; color: #ffffff;`
  const tdChatStyle = `padding: 4px ; min-width: 10px;`
  const tdButtonAreaStyle = `padding: 8px ; min-width: 10px ; background-color: #ffebd6; text-align: right ; margin: 4px 4px 8px;`

  const openBox = "<div style='margin-top: 40px;color: #000; border: 1px solid #000; background-color: #FFEBD6; box-shadow: 0 0 3px #000; display: block; text-align: left; font-size: 13px; padding: 2px; margin-bottom: 2px; font-family: sans-serif; white-space: pre-wrap;'>";
  const openMenuBox = "<div style='position:fixed; top:0px left:0px; height:30px; color: #000; border: 1px solid #000; background-color: #FFEBD6; box-shadow: 0 0 2px #000; display: block; text-align: left; font-size: 13px; padding: 2px; margin-bottom: 2px; font-family: sans-serif; white-space: pre-wrap;'>";    
  const closeBox = '</div>';
  const openScrollBox = `<div style='height:80vh; overflow-y: scroll; border: 1px solid black; padding 5px; display: block;'>`
  const openScrollCharBox = `<div style='height:${state.DMDashboard.charBox_ScrollHeight}; overflow-y: scroll; border: 1px solid black; padding 5px; display: block;'>`
  const openScrollTOBox = `<div style='height:${state.DMDashboard.toBox_ScrollHeight}; overflow-y: scroll; border: 1px solid black; padding 5px; display: block;'>`
  const closeScrollBox = '</div>';
  const sheetURL = 'http://journal.roll20.net/character/';
  const profileURL = 'https://app.roll20.net/users/';
  const handoutURL = 'https://app.roll20.net/handout';
  const tableURL = `!&#10;/roll 1t[`;
  const toReportName = 'DM Turnoder List'
  const CharSheetName = 'DM Character Sheet'
  const dmNotesReportName = 'DM Notes'
  const dmJukeBoxName = 'DM Jukebox';
  const debugLogName = 'DM Debug Log'
  let Exp_ndx = '';
  let ndx = '';

  const emojiClear = '\u{274C}' // ❌
  const emojiLink = '\u{1F517}' // 🔗
  const emojiHide = '\u{1F47B}' // 👻 🫥
  // const emojiShow = '\u{1F611}' // 😑
  const emojiShow = '\u{D83D}\u{DE4B}\u{D83C}\u{DFFB}\u{200D}\u{2640}\u{FE0F}' // 🙋🏻‍♀️
  const emojiPing = '\u{1F3AF}' // 🎯
  const emojiDocument = '\u{1F4D1}' // 📑
  const emojiNote = '\u{1F4D3}' // 📓
  const emojiEdit = '\u{1F58A}' // 🖊
  const emojiLock = '\u{1F512}' // 🔒
  const emojiUnlock = '\u{1F513}' // 🔓
  const emojiNameplate = '\u{1F4DB}' // 📛
  const emojiAvatar = '\u{1F464}' // 👤
  const emojiImages = '\u{1F5BC}' //🖼
  const emojiFriend = '\u{1F607}' //😇
  const emojiFoe = '\u{1F621}' // 😡
  const emojiNeutral = '\u{1F4A9}' //💩
  const emojiPlus = '\u{2795}' // ➕
  const emojiMinus = '\u{2796}' // ➖
  const emojiFilter = '\u{1F50E}';  // 🔎 
  const emojiFav = '\u{2764}\u{FE0F}' // ❤️ 
  const emojiNotFav = '\u{1F90D}' // 🤍
  const emojiPlay =  '\u{25B6}\u{FE0F}' // ▶️ 
  const emojiPause =  '\u{25B6}\u{FE0F}'; // ⏸️ 
  const emojiPlayPause =  '\u{23EF}\u{FE0F}'; // ⏯️ 
  const emojiRewind =  '\u{23EA}'; // ⏪ 
  const emojiFastForward =  '\u{23E9}'; // ⏩ 
  const emojiStop =  '\u{23F9}\u{FE0F}'; // ⏹️ 
  const emojiPrevTrack =  '\u{23EE}\u{FE0F}'; // ⏮️ 
  const emojiNextTrack =  '\u{23ED}\u{FE0F}'; // ⏭️ 
  const emojiRewindLeft =  '\u{21A9}\u{FE0F}'; // ↩️ 
  const emojiRewindRight =  '\u{21AA}\u{FE0F}'; // ↪️
  const emojiVolumeUp1 ='\u{D83D}\u{DD3C}'  // 🔼
  const emojiVolumeUp10 ='\u{23EB}' // ⏫
  const emojiVolumeDown1 ='\u{D83D}\u{DD3D}' //🔽
  const emojiVolumeDown10 ='\u{23EC}' //⏬

  const emojiLoop = '\u{D83D}\u{DD01}'; // 🔁
  const emojiEffect = '\u{D83D}\u{DCA5}'; //💥
  const emojiAmbiance = '\u{D83C}\u{DFB6}'; // 🎶
  const emojiEmptyBox = '\u{2B1C}\u{FE0F}'; // 



  let chatMsg = '';

  // Parse Args and Commands
  let args = msg_content.split(/\s--/);
  //let commands = args[1].split(/\s+/);
  let commands = [];
  if (args.length > 1) {
    commands = args[1].match(/(?:[^\s"']+|"[^"]*")+/g);
    commands = commands.map(item => item.replace(/^"|"+$/g, ''));
  } else {
    commands.push('OPEN');
  }

  myDebug(3, `MsgHandler: args: ${args[0]}`)
  myDebug(2, `MsgHandler: msg_content: ${msg_content}`)
  myDebug(3, `MsgHandler: commands: ${commands}`)
  commands.forEach(c => {
    myDebug(2, `MsgHandler: command: ${c}`);
  });

  commands[0] = commands[0].toUpperCase();
  myDebug(3, `MsgHandler: commands[0]: ${commands[0]}`)  
  

  let masterCmd = args[0].toUpperCase()

  if (masterCmd =='!DMDASH') {
    switch (commands[0]) {

      case 'TO-CLEAR':
        to_Clear();
        updateTurnOrderStartTime();
        refreshReports();
        break;
      case 'TO-NEXT':
        to_MoveNext();
        refreshReports();
        break;
      case 'TO-PREV':
        to_MovePrev();
        updateTurnOrderStartTime();
        refreshReports();
        break;
      case 'TO-SORT':
        to_Sort();
        updateTurnOrderStartTime();
        refreshReports();
        break;
      case 'TO-SORTWRAPPED':
        to_SortWrapped();
        updateTurnOrderStartTime();
        refreshReports();
        break;
      case 'TO-ADDCUSTOM': // Position, Name
        to_AddCustom(commands[2], commands[1])
        refreshReports();
        break;
      case 'TO-ADDROUND': //No Parameters
        // log(`TO-AddRound`)
        to_AddCustom('>>>Round<<<', 1, '+1')
        refreshReports();
        break;
      case 'TO-ADDCOUNTDOWN': // Direction, Starting Pos, Name
        // log(`TO-AddCustom: Cmd1:${commands[1]} Cmd2:${commands[2]} Cmd3:${commands[3]} Cmd4:${commands[4]}`)
        to_AddCustom(commands[3], commands[2], commands[1])
        refreshReports();
        break;
      case 'TO-REMOVE':
        to_Remove(commands[1]); // Assumes that the next the command is "!DMDash --to-Remove tid"
        updateTurnOrderStartTime();
        refreshReports();
        break;
      case 'TO-REMOVECUSTOM':
        to_RemoveCustom(commands[1]); // Assumes that the next the command is "!DMDash --to-RemoveCustom ndx"
        updateTurnOrderStartTime();
        refreshReports();
        break;
      case 'ADDCHARGMNOTE': // charId, Name
        addTextToCharGMNote(commands[1], commands[2])
        refreshReports();
        break;
      case 'ADDTOKENGMNOTE': // tokenId, Name
        addTextToTokenGMNote(commands[1], commands[2])
        refreshReports();
        break;
      case 'ADDGMNOTE': // Add note to GMNote are of the Turnorder List
        addTextToGMNote(commands[1])
        break;
      case 'RESETSTATS':
        ResetStats();
        break;
      case 'INITIATIVE':
        addInitiative(commands[1]);
        refreshReports();
        break;
      case 'TOKENTOGGLEVISABITY':
        tokenToggleVisibility(commands[1])
        refreshReports();
        break;
      case 'TOKENTOGGLELOCK':
        tokenToggleLock(commands[1])
        refreshReports();
        break;
      case 'TOKENCLEARTOOLTIP':
        tokenClearTooltip(commands[1])
        refreshReports();
        break;
      case 'TOKENTOGGLETOOLTIP':
        tokenToggleTooltip(commands[1])
        refreshReports();
        break;
      case 'TOKENCLEARTOOLTIP':
        tokenClearTooltip(commands[1])
        refreshReports();
        break;
      case 'TOKENSETTOOLTIP':
        tokenSetTooltip(commands[1], commands[2])
        refreshReports();
        break;
      case 'TOKENTOGGLENAMEPLATE':
        tokenToggleNameplate(commands[1]);
        refreshReports();
        break;
      case 'TOGGLEFRIEND':
        toggleFriendFoe(commands[1]); 
        refreshReports();
        break;
      case 'TOKENADJUSTHP':
        tokenAdjHP(commands[1], commands[2]);
        refreshReports();
        break;
      case 'PINGTOKEN-GM':
        pingToken(commands[1],0);
        break;
      case 'PINGTOKEN-ALL':
        pingToken(commands[1],1);
        break;
      case 'SHOWAVATAR':
        myDebug(3, `ShowAvatar - Got to msghandler: ${commands[1]}, ${commands[2]}, ${commands[3]}`)
        showAvatar(commands[1], commands[2], commands[3]); //tknId, ShowTitle? (0/1), Whisper? (0/1)
        break;
      case 'SHOWIMAGE':
        myDebug(3, `ShowImage - Got to msghandler: ${commands[1]}, ${commands[2]}, ${commands[3]}, ${commands[4]}`)
        showCharImage(commands[1], commands[2], commands[3], commands[4]); //tknId, imgIndx(0 for all), ShowTitle? (0/1), Whisper? (0/1)
        break;
      case 'SHOWGMNOTE':
        myDebug(3, `ShowGMNote - Got to msghandler: ${commands[1]}`)
        showGMNote(commands[1], 0); //tknId, Flag 0-NPC: Token.GMNote PC: Char.GMNote, 1-Token.GMNote, 2-Char.GMNote
        break;
      case 'TOREPORT':
        // *** Process subcommands like 'expand' and 'collapse' ***
        Exp_ndx = msg_content.indexOf('expand');
        if(Exp_ndx>0){
          state.DMDashboard.DetailExpand = 1;
        }
        Exp_ndx = msg_content.indexOf('collapse');
        if(Exp_ndx>0){
          state.DMDashboard.DetailExpand = 0;
        }
        refreshReports();
        break;
      case 'HPBAR':
        setHPBar(commands[1]);
        refreshReports();
        break;
      case 'SBHEIGHT':
        setSBHeights(commands[1], commands[2]);  // Character Box Height, Turnorder Box Height
        refreshReports();
        break;
      case 'OPEN':
      case 'SHOW-HO-DIALOG':
        chatMsg = `<b>&nbsp;&nbsp;[DM Dashboard](https://journal.roll20.net/handout/${getHandout('DM Turnoder List').get('_id')})`;
        chatMsg += `<br>&nbsp;&nbsp;[DM Character Sheet](https://journal.roll20.net/handout/${getHandout('DM Character Sheet').get('_id')})`;
        chatMsg += `<br>&nbsp;&nbsp;[DM Notes](https://journal.roll20.net/handout/${getHandout('DM Notes').get('_id')})`;      
        chatMsg += `<br>&nbsp;&nbsp;[DM Jukebox](https://journal.roll20.net/handout/${getHandout('DM Jukebox').get('_id')})`;      
        chatMsg += `<br>&nbsp;&nbsp;[DM Turnorder Log](https://journal.roll20.net/handout/${getHandout('DM Turnorder Log').get('_id')})</b>`;      

        // sendChat("DM Dashboard", chatMsg);
        mySendChat(true, "DM Dashboard", chatMsg)
        refreshReports();
        listTracks();
        buildDMNotesHandout();
        break;

      case 'FLUSHDATALOG':
        flushDataLog();
        state.DMDashboard.NotesRpt_FavsAry = [];
        break;

      case 'DATALOGGING':
        if (commands[1] == 0 || commands[1] == false || commands[1] == 'false' || commands[1] == 'off'){
          state.DMDashboard.DataLogging = false;
          mySendChat(true, "DM Dashboard", `Data Logging turned off`);
        } else {
          state.DMDashboard.DataLogging = true;
          mySendChat(true, "DM Dashboard", `Data Logging turned on`);
        }
        break;

      case 'NOTIFICATIONS':
        if (commands[1] == 0 || commands[1] == false || commands[1] == 'false' || commands[1] == 'off'){
          state.DMDashboard.TurnNotification = false;
          mySendChat(true, "DM Dashboard", `Turn Notifications turned off`);
        } else {
          state.DMDashboard.TurnNotification = true;
          mySendChat(true, "DM Dashboard", `Turn Notifications turned on`);
        }
        break;  

      case 'CLEARCACHE':
        charMap.clear();
        mySendChat(true, "DM Dashboard", `Cache cleared`);
        refreshReports();
        break;

      case 'LOGCHAT':
        if (commands[1] == 0 || commands[1] == false || commands[1] == 'false' || commands[1] == 'off'){
          state.DMDashboard.LogChat = false;
          mySendChat(true, "DM Dashboard", `Logging Chat turned <b>OFF</b>.`);
        } else {
          state.DMDashboard.LogChat = true;
          mySendChat(true, "DM Dashboard", `Logging Chat turned <b>ON</b>.`);
        }

        break;
      case 'DEBUG':
          // 0 - Off, 1 - API Log, 2 - Chat, 3 - Chat & Log, 4 - Handout(Currently has issue writing quickly to handout)
          // 4; //0 - All, 1 - Low Info, 2 - High Info, 3 - Basic Debug, 4 - New Code Debug
        if (commands[1]) {
          state.DMDashboard.Debug = commands[1];
        }
        if (commands[2]) {
          state.DMDashboard.DebugLvl = commands[2];
        }
        mySendChat(true, "DM Dashboard", `Debugging set to ${commands[1]}.  Level set to ${commands[2]}.`);
        break;
      case 'HELP':
        dmdash_Help();
        break;
      
      case 'CONFIG':
        dmdash_ConfigSettings();
        break;

      case 'PLAYERSTATS':
        showPlayerStats(commands[1]);
        break;

      case 'PLAYERACCESS':
        getPlayerAccess();
        break;
      case 'TRACK':
      case 'TRACKS':
      case 'LISTTRACKS':        
        listTracks();
        break;

      case 'JBALL':
        state.DMDashboard.JB_Tier1MenuSelected = "Jukebox"
        listTracks();
        break;
      case 'JBFAVS':
        state.DMDashboard.JB_Tier1MenuSelected = "Favorites"        
        listTracks();
        break;

      case 'PLAYTRACK':
        playTrack(commands[1]);
        listTracks();
        break;
  
      case 'SETDASHTRACK':
        setDashTrack(commands[1], commands[2]);
        listTracks();
        break;

      case 'TOGGLEAMBIANCETRACK':
        ndx = state.DMDashboard.JB_AmbianceFavs.indexOf(commands[1]);
        if (ndx<0) {
          state.DMDashboard.JB_AmbianceFavs.push(commands[1]);
          myDebug(4, `AmbTrack On: ${commands[1]} count: ${state.DMDashboard.JB_AmbianceFavs.length}`);
        } else {
          state.DMDashboard.JB_AmbianceFavs.splice(ndx,1);
          myDebug(4, `AmbTrackOff: ndx:${ndx} ${commands[1]} count: ${state.DMDashboard.JB_AmbianceFavs.length}`);
        }
        listTracks();        
        break;

      case 'TOGGLEEFFECTTRACK':
        ndx = state.DMDashboard.JB_EffectsFavs.indexOf(commands[1]);
        if (ndx<0) {
          state.DMDashboard.JB_EffectsFavs.push(commands[1]);
          myDebug(4, `AmbTrackOn: ${commands[1]} count: ${state.DMDashboard.JB_EffectsFavs.length}`);
        } else {
          state.DMDashboard.JB_EffectsFavs.splice(ndx,1);
          myDebug(4, `AmbTrackOff: ndx:${ndx} ${commands[1]} count: ${state.DMDashboard.JB_EffectsFavs.length}`);
        }
        listTracks();        
      break;

      case 'LOOPTRACK':
        setLoopTrack(commands[1], commands[2]);
        listTracks();
        break;

      case 'SETVOLUME':
        setVolume(commands[1], commands[2]);
        listTracks();
        break;


      default:
        mySendChat(true, "DM Dashboard", `Command ${commands[0]} not recognized.`)
    }
  } else if (masterCmd == '!DMNOTES') {

    switch (commands[0]) {
      case 'BUILD':
        state.DMDashboard.NotesRpt_Tier1MenuSelected = 'Tokens'
        state.DMDashboard.NotesRpt_SelectedId = ''
        buildDMNotesHandout();
        break;
      case 'TOKENS':
        state.DMDashboard.NotesRpt_Tier1MenuSelected = 'Tokens'
        // state.DMDashboard.NotesRpt_SelectedId = ''
        buildDMNotesHandout();
        break;
      case 'CHARS':
        state.DMDashboard.NotesRpt_Tier1MenuSelected = 'Characters'
        // state.DMDashboard.NotesRpt_SelectedId = ''        
        buildDMNotesHandout();
        break;
      case 'HANDOUTS':
        state.DMDashboard.NotesRpt_Tier1MenuSelected = 'Handouts'
        // state.DMDashboard.NotesRpt_SelectedId = ''        
        buildDMNotesHandout();
        break;
      case 'ROWSELECTED':
        myDebug(3, 'RowSelected: ' + commands[1]);
        state.DMDashboard.NotesRpt_SelectedId = commands[1];
        myDebug(4, `Selected: ${commands[1]}`);
        buildDMNotesHandout();
        break;

      case 'TOGGLEFAV':
        ndx = state.DMDashboard.NotesRpt_FavsAry.indexOf(commands[1]);
        if (ndx<0) {
          state.DMDashboard.NotesRpt_FavsAry.push(commands[1]);
          myDebug(3, `FavsOn: ${commands[1]} count: ${state.DMDashboard.NotesRpt_FavsAry.length}`);
        } else {
          state.DMDashboard.NotesRpt_FavsAry.splice(ndx,1);
          myDebug(3, `FavsOff: ndx:${ndx} ${commands[1]} count: ${state.DMDashboard.NotesRpt_FavsAry.length}`);
        }
        buildDMNotesHandout();
        break;
      case 'CLEARFAV':
        state.DMDashboard.NotesRpt_FavsAry = [];
        state.DMDashboard.NotesRpt_FavsOn = 0;
        buildDMNotesHandout();1
        break;
  
      case 'FAVSON':
        myDebug(4, 'FavsOn: ' + commands[1]);
        state.DMDashboard.NotesRpt_FavsOn = commands[1];
        buildDMNotesHandout();
        break;
      case 'FILTER':
        if (!commands[1] || commands[1].lenght == 0) {
          state.DMDashboard.NotesRpt_Filter = '';
        } else {
          state.DMDashboard.NotesRpt_Filter = commands[1];
        }
        buildDMNotesHandout();
        break;

      default:
        mySendChat(true, "DM Notes", `Command ${commands[0]} not recognized.`)

    }
  }

  reportPerformance('Function execution time'); 
}

{try{throw new Error('');}catch(e){API_Meta.DMDashboard.lineCount=(parseInt(e.stack.split(/\n/)[1].replace(/^.*:(\d+):.*$/,'$1'),10)-API_Meta.DMDashboard.offset);}}