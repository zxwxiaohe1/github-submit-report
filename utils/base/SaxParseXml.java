package com.si;
        import com.sinux.addon.ci.entity.FailureCaseInfo;
        import com.sinux.addon.ci.entity.TestReport;
        import org.xml.sax.Attributes;
        import org.xml.sax.SAXException;
        import org.xml.sax.helpers.DefaultHandler;

        import javax.xml.parsers.SAXParser;
        import javax.xml.parsers.SAXParserFactory;
        import java.io.File;
        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;

/**
 * @ContentDiscrible: dom解析测试报告
 * @Author: Created by heyong.
 * @CreateTime: on 2017/9/14.
 */
public class SaxParseXml {

    public TestReport readXML(String modulePath,String xmls)throws Exception{
        List<TestReport> Reports=new ArrayList<TestReport>();

        SAXParserFactory factory=SAXParserFactory.newInstance();
        SAXParser parser=factory.newSAXParser();
//        parser.parse(new File(modulePath),handler);
        List<File> targetFiles=new FileStack().getTargetFiles(modulePath,xmls.trim());
        for (File file:targetFiles){
            XmlConfigParser handler=new XmlConfigParser();
            parser.parse(file,handler);
            Reports.add(handler.getReport());
        }
        return getFinalReport(Reports);
    }
    public TestReport getFinalReport(List<TestReport> Reports){
        List<TestReport> Repor=Reports;

        for (int i=0;i<Reports.size();i++){
            System.out.println(Reports.get(i).getSummation().toString());
            List<FailureCaseInfo> fi=Reports.get(i).getFailureCases();
            for(int j=0;j<fi.size();j++) {
                System.out.println(fi.get(j).getClassName().toString());
                System.out.println(fi.get(j).getErrorMethod());
            }
        }
        TestReport  Report=new  TestReport();
        if (Reports.size()!=1){
            Map<String,Integer> summation=new HashMap<String, Integer>();
            int skipped=0,failed=0,total=0,passed=0;
            List<FailureCaseInfo> failureCases=new ArrayList<FailureCaseInfo>();
            for(TestReport rt:Reports){
                skipped+=rt.getSummation().get("skipped");
                failed+=rt.getSummation().get("failed");
                total+=rt.getSummation().get("total");
                passed+=rt.getSummation().get("passed");
                StringBuffer addMethod=new StringBuffer();
                for(FailureCaseInfo f:rt.getFailureCases()) {
                    boolean IncErrClass=false;
                    for (FailureCaseInfo c : failureCases) {
                        if (f.getClassName().equals(c.getClassName())) {
                            addMethod.append(c.getErrorMethod()).append(f.getErrorMethod());
                            c.setErrorMethod(addMethod.toString());
                            IncErrClass=true;
                            break;
                        }
                    }
                    if(!IncErrClass){
                        failureCases.add(f);
                    }
                }

            }
            summation.put("skipped",skipped);
            summation.put("failed",failed);
            summation.put("total",total);
            summation.put("passed",passed);
            Report.setSummation(summation);
            Report.setFailureCases(failureCases);
        }else {
            Report=Reports.get(0);
        }
        return Report;
    }
    public static void main(String [] args)throws Exception{
        SaxParseXml saxParseXml=new SaxParseXml();
        System.out.println(saxParseXml.readXML("F:\\WorkTest\\idea\\jfusion-core\\target\\surefire-reports","testng-results.xml").getMailContent("jfusion-core"));
    }
}

class XmlConfigParser extends DefaultHandler{

    private TestReport testReport=null;
    private Map<String,Integer> summation=null;
    private List<FailureCaseInfo> failureCases=null;
    private FailureCaseInfo failureCase=null;
    private String className="";
    private String Method="";
    //排除unitils自身测试方法
    private final List<String> excludeMethod=new ArrayList<String>();
    public XmlConfigParser(){
        this.testReport =new TestReport();
        this.summation=new HashMap<String,Integer>();
        this.failureCases=new ArrayList<FailureCaseInfo>();
        this.excludeMethod.add("unitilsBeforeClass");
        this.excludeMethod.add("unitilsBeforeTestSetUp");
        this.excludeMethod.add("unitilsAfterTestTearDown");
        this.excludeMethod.add("unitilsAfterTestTearDown");
    }
    public TestReport getReport(){
        return this.testReport;
    }
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if(qName.equals("testng-results")){
            this.summation.put("skipped",Integer.parseInt(attributes.getValue("skipped")));
            this.summation.put("failed",Integer.parseInt(attributes.getValue("failed")));
            this.summation.put("total",Integer.parseInt(attributes.getValue("total")));
            this.summation.put("passed",Integer.parseInt(attributes.getValue("passed")));
        }else if(qName.equals("class")){
            this.failureCase=new FailureCaseInfo();
            this.className=attributes.getValue("name");
        }else if(qName.equals("test-method")){
            if(attributes.getValue("status").equals("FAIL")){
                String failMethod=attributes.getValue("name");
                if(!excludeMethod.contains(failMethod)){
                    this.failureCase.setClassName(this.className);
                    Method+=failMethod+",";
                }

            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if(qName.equals("class")){
//           if(StringUtils.isNotEmpty(this.Method)){
            if(this.Method!=null&&this.Method!=""){
                this.failureCase.setErrorMethod(this.Method);
                this.failureCases.add(this.failureCase);
            }
            this.failureCase=null;
            this.className="";
            this.Method="";
        }
        else if(qName.equals("testng-results")){
            this.testReport.setSummation(this.summation);
            this.testReport.setFailureCases(this.failureCases);
        }
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        this.summation=null;
        this.failureCases=null;
    }
}