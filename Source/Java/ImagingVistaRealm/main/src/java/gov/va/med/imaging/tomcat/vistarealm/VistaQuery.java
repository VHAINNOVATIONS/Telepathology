/*
 * Created on Jan 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.imaging.tomcat.vistarealm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author VHAANNGilloJ
 *
 * 
 */
public class VistaQuery 
implements Cloneable
{
    private String rpcName = null;
    private ArrayList<Parameter> parameters = null;

    public static final int LITERAL = 1;
    public static final int REFERENCE = 2;
    public static final int LIST = 3;
    public static final int WORDPROC = 4;

    public VistaQuery(String rpcName)
    {
        setRpcName(rpcName);
    }

    public VistaQuery()
    {
    	setRpcName(null);
    }

    public void setRpcName(String rpcName)
    {
        this.rpcName = rpcName;
        this.parameters = new ArrayList<Parameter>();		// is thie really intentional?  setting the RPC wacks the parameter list ?
    }

    public void clear()
    {
        this.rpcName = "";
        this.parameters.clear();
    }

    /**
     * A clone that at least follows the specification for the clone() method.
     * Copies deep up to the parameteter values.  List type parameters are 
     * cloned, but the list members are not.
     */
    @Override
	public Object clone() 
    throws CloneNotSupportedException
	{
		VistaQuery clone = new VistaQuery(this.getRpcName());
		
		for( Iterator<Parameter> parameterIter = parameters.iterator(); parameterIter.hasNext(); )
		{
			Parameter parameter = (Parameter)parameterIter.next();
			clone.parameters.add( (Parameter)(parameter.clone()) );
		}
		
		return clone;
	}

	public void clone(VistaQuery msg) 
    {
        this.rpcName = msg.getRpcName();
        this.parameters = (ArrayList<Parameter>)msg.parameters.clone();
    }

    public String getRpcName()
    {
        return this.rpcName;
    }

    public int getParamCount()
    {
        return parameters.size();
    }

    public ArrayList getParams() 
    {
        return parameters;
    }

    public void addParameter(int type, String value)
    {
        Parameter vp = new Parameter(type,value);
        parameters.add(vp);
    }

    public void addParameter(int type,String value,String text)
    {
        Parameter vp = new Parameter(type,value,text);
        parameters.add(vp);
    }

	public void addParameter(int type, Map lst)
	{
		Parameter vp = new Parameter(type,lst);
		parameters.add(vp);
	}

    public void addEncryptedParameter(int type, String value) 
    {
        Parameter vp = new Parameter(type, EncryptionUtils.encrypt(value));
        parameters.add(vp);
    }

    public void changeParameterValue(int idx, String value) 
    {
        Parameter p = (Parameter)parameters.get(idx);
        p.setValue(value);
    }

    private String buildApi(String rpcName, String params, String fText)
    {
        String sParams = strPack(params,5);
        return strPack(fText + rpcName + '^' + sParams,5);
    }

    public String buildMessage()
    {
        final String PREFIX = "{XWB}";
        final String HDR = "007XWB;;;;";
        //final char NEWLINE = '|';

        String sParams = "";
        //String sText = "";
		Map lst = null;
        //Iterator iter = parameters.iterator();
        //while (iter.hasNext())
		for (int i=0; i<parameters.size(); i++)
        {
            //Parameter vp = (Parameter)iter.next();
			Parameter vp = (Parameter)parameters.get(i);
            int pType = vp.getType();
            if (pType == LITERAL) 
            {
                sParams += strPack('0' + vp.getValue(),3);
            }
            else if (pType == REFERENCE) 
            {
                sParams += strPack('1' + vp.getValue(),3);
            }
            else if (pType == LIST)
            {
                sParams += strPack('2' + vp.getValue(),3);
				lst = vp.getList();
//                String txt = vp.getText();
//                if (txt.charAt(txt.length()-1) != NEWLINE) 
//                {
//                	txt += NEWLINE;
//                }
//                Vector v = makeLines(txt,NEWLINE);
//                Enumeration lines = v.elements();
//                int linenum = 0;
//                if (pType == LIST) 
//                {
//                	linenum = 1;
//                }
//                while (lines.hasMoreElements())
//                {
//                    String line = (String)lines.nextElement();
//                    sText += strPack(Integer.toString(linenum++),3);
//                    sText += strPack(line,3);
//                }
//                sText += "000";
            }
        }
        String msg = "";
//        if (sText.equals(""))
//        {
//            msg = strPack(HDR + buildApi(rpcName,sParams,"0"),5);
//            return PREFIX + strPack(msg,5);
//        }
//        else
//        {
//            msg = strPack(HDR + buildApi(rpcName,sParams,"1"),5);
//            return PREFIX + strPack(msg + sText,5);
//        }
		if (lst == null) 
		{
			msg = strPack(HDR + buildApi(rpcName,sParams,"0"),5);
		} 
		else 
		{
			msg = strPack(HDR + buildApi(rpcName,sParams,"1"),5);
			Iterator iter = lst.keySet().iterator();
			while (iter.hasNext()) 
			{
				String key = (String)iter.next();
				String value = (String)lst.get(key);
				if (value == null || value.equals(""))
					value = "\u0001";
				msg += strPack(key,3) + strPack(value,3);
			}
			msg += "000";
		}
		msg = PREFIX + strPack(msg,5);
		return msg;
    }

//    private Vector makeLines(String s, char delim)
//    {
//        final String CRLF = "\r\n";
//        Vector v = new Vector();
//        int p1 = 0;
//        int p2 = 0;
//        while (p2 != -1)
//        {
//            p2 = s.indexOf(delim,p1);
//            if (p2 != -1)
//            {
//                String line = s.substring(p1,p2);
//                if (line.length() == 0) 
//                {
//                	line = CRLF;
//                }
//                while (line.length() > 72)
//                {
//                    String l = line.substring(0,71);
//                    int p3 = l.lastIndexOf(' ');
//                    l = l.substring(0,p3);
//                    v.addElement(l);
//                    line = line.substring(p3+1);
//                }
//                v.addElement(line);
//                p1 = p2 + 1;
//            }
//        }
//        return v;
//    }

    public static String strPack(String s, int n)
    {
        Integer lth = new Integer(s.length());
        String result = lth.toString();
        while (result.length() < n) 
        {
        	result = '0' + result;
        }
        return result + s;
    }
    
    
    
    private final String[] cipherPad =
    {
        "wkEo-ZJt!dG)49K{nX1BS$vH<&:Myf*>Ae0jQW=;|#PsO`\'%+rmb[gpqN,l6/hFC@DcUa ]z~R}\"V\\iIxu?872.(TYL5_3",
        "rKv`R;M/9BqAF%&tSs#Vh)dO1DZP> *fX\'u[.4lY=-mg_ci802N7LTG<]!CWo:3?{+,5Q}(@jaExn$~p\\IyHwzU\"|k6Jeb",
        "\\pV(ZJk\"WQmCn!Y,y@1d+~8s?[lNMxgHEt=uw|X:qSLjAI*}6zoF{T3#;ca)/h5%`P4$r]G\'9e2if_>UDKb7<v0&- RBO.",
        "depjt3g4W)qD0V~NJar\\B \"?OYhcu[<Ms%Z`RIL_6:]AX-zG.#}$@vk7/5x&*m;(yb2Fn+l\'PwUof1K{9,|EQi>H=CT8S!",
        "NZW:1}K$byP;jk)7\'`x90B|cq@iSsEnu,(l-hf.&Y_?J#R]+voQXU8mrV[!p4tg~OMez CAaGFD6H53%L/dT2<*>\"{\\wI=",
        "vCiJ<oZ9|phXVNn)m K`t/SI%]A5qOWe\\&?;jT~M!fz1l>[D_0xR32c*4.P\"G{r7}E8wUgyudF+6-:B=$(sY,LkbHa#\'@Q",
        "hvMX,\'4Ty;[a8/{6l~F_V\"}qLI\\!@x(D7bRmUH]W15J%N0BYPkrs&9:$)Zj>u|zwQ=ieC-oGA.#?tfdcO3gp`S+En K2*<",
        "jd!W5[];4\'<C$/&x|rZ(k{>?ghBzIFN}fAK\"#`p_TqtD*1E37XGVs@0nmSe+Y6Qyo-aUu%i8c=H2vJ\\) R:MLb.9,wlO~P",
        "2ThtjEM+!=xXb)7,ZV{*ci3\"8@_l-HS69L>]\\AUF/Q%:qD?1~m(yvO0e\'<#o$p4dnIzKP|`NrkaGg.ufCRB[; sJYwW}5&",
        "vB\\5/zl-9y:Pj|=(R\'7QJI *&CTX\"p0]_3.idcuOefVU#omwNZ`$Fs?L+1Sk<,b)hM4A6[Y%aDrg@~KqEW8t>H};n!2xG{",
        "sFz0Bo@_HfnK>LR}qWXV+D6`Y28=4Cm~G/7-5A\\b9!a#rP.l&M$hc3ijQk;),TvUd<[:I\"u1\'NZSOw]*gxtE{eJp|y (?%",
        "M@,D}|LJyGO8`$*ZqH .j>c~h<d=fimszv[#-53F!+a;NC\'6T91IV?(0x&/{B)w\"]Q\\YUWprk4:ol%g2nE7teRKbAPuS_X",
        ".mjY#_0*H<B=Q+FML6]s;r2:e8R}[ic&KA 1w{)vV5d,$u\"~xD/Pg?IyfthO@CzWp%!`N4Z\'3-(o|J9XUE7k\\TlqSb>anG",
        "xVa1\']_GU<X`|\\NgM?LS9{\"jT%s$}y[nvtlefB2RKJW~(/cIDCPow4,>#zm+:5b@06O3Ap8=*7ZFY!H-uEQk; .q)i&rhd",
        "I]Jz7AG@QX.\"%3Lq>METUo{Pp_ |a6<0dYVSv8:b)~W9NK`(r\'4fs&wim\\kReC2hg=HOj$1B*/nxt,;c#y+![?lFuZ-5D}",
        "Rr(Ge6F Hx>q$m&C%M~Tn,:\"o\'tX/*yP.{lZ!YkiVhuw_<KE5a[;}W0gjsz3]@7cI2\\QN?f#4p|vb1OUBD9)=-LJA+d`S8",
        "I~k>y|m};d)-7DZ\"Fe/Y<B:xwojR,Vh]O0Sc[`$sg8GXE!1&Qrzp._W%TNK(=J 3i*2abuHA4C\'?Mv\\Pq{n#56LftUl@9+",
        "~A*>9 WidFN,1KsmwQ)GJM{I4:C%}#Ep(?HB/r;t.&U8o|l[\'Lg\"2hRDyZ5`nbf]qjc0!zS-TkYO<_=76a\\X@$Pe3+xVvu",
        "yYgjf\"5VdHc#uA,W1i+v\'6|@pr{n;DJ!8(btPGaQM.LT3oe?NB/&9>Z`-}02*%x<7lsqz4OS ~E$\\R]KI[:UwC_=h)kXmF",
        "5:iar.{YU7mBZR@-K|2 \"+~`M%8sq4JhPo<_X\\Sg3WC;Tuxz,fvEQ1p9=w}FAI&j/keD0c?)LN6OHV]lGy\'$*>nd[(tb!#"
    };

    /*
    
    public String encrypt(String inString)
    {
        final int MAXKEY = 19;
        
        Random r = new Random();
        int associatorIndex = r.nextInt(MAXKEY);
        int identifierIndex = r.nextInt(MAXKEY);
        while (associatorIndex == identifierIndex) 
        {
            identifierIndex = r.nextInt(MAXKEY);
        }
        String xlatedString = "";
        for (int i=0; i < inString.length(); i++)
        {
            char inChar = inString.charAt(i);
            int pos = cipherPad[associatorIndex].indexOf(inChar);
            if (pos == -1) 
            {
                xlatedString += inChar;
            }
            else 
            {
                xlatedString += cipherPad[identifierIndex].charAt(pos);
            }
        }
        return (char)(associatorIndex + 32) +
            xlatedString +
            (char)(identifierIndex + 32);
    }
    
 
    
    public String decrypt(String inString) {
    	int identifierIndex = (int)inString.charAt(0) - 32;
    	int associatorIndex = (int)inString.charAt(inString.length() - 1) - 32;
    	
    	String xlatedString = "";
    	
    	for(int i = 1; i < inString.length() - 1; i++) {
    		char inChar = inString.charAt(i);
            int pos = cipherPad[associatorIndex].indexOf(inChar);
            if (pos == -1) 
            {
                xlatedString += inChar;
            }
            else 
            {
                xlatedString += cipherPad[identifierIndex].charAt(pos);
            }
    	}
    	
    	return xlatedString;
    }
    */

    public class Parameter
    implements Cloneable
    {
        private int type;
        private String value;
        private String text;
		private Map lst;

        public Parameter()
        {
            this.type = -1;
            this.value = "";
        }

        public Parameter(int type, String value)
        {
            this.type = type;
            this.value = value;
        }

        public Parameter(int type, String value, String text)
        {
            this.type = type;
            this.value = value;
            this.text = text;
        }

		public Parameter(int type, Map lst) 
		{
			this.type = type;
			this.value = ".x";
			this.lst = lst;
		}

        public void setType(int type)
        {
            this.type = type;
        }

        public int getType()
        {
            return this.type;
        }

        public void setValue(String value)
        {
            this.value = value;
        }

        public String getValue()
        {
            return this.value;
        }

        public void setText(String text)
        {
            this.text = text;
        }

        public String getText()
        {
            return this.text;
        }

		public void setList(Map lst) 
		{
			this.lst = lst;
		}

		public Map getList() 
		{
			return lst;
		}

		protected Object clone() 
		throws CloneNotSupportedException
		{
			Parameter clone = new Parameter();
			
			clone.type = this.type;
			clone.lst = new HashMap( this.lst );
			clone.text = new String( this.text );
			clone.value = this.value;
			
			return clone;
		}
		
		
    }
}
