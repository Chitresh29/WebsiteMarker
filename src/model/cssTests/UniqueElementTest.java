package model.cssTests;

import jdk.nashorn.api.tree.CompilationUnitTree;
import model.TestResult;
import model.Testable;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;

import java.util.ArrayList;

public class UniqueElementTest extends Testable {

    UniqueElement divSpan;    //Div or span depending on enum argument

    public UniqueElementTest(UniqueElement divSpan) {
        super(divSpan.name());
        this.divSpan = divSpan;
    }

    @Override
    public TestResult runTest(ArrayList<Document> documents, CSSStyleSheet sheet, CompilationUnitTree tree, double percentage) {
        double result = 0;
        String report = "";

        for (Document document : documents) {
            Elements elements = document.select(divSpan.htmlAttribute);
            for (Element element : elements) {
                CSSStyleRule rule;
                if (element.attr("class").length() > 0) {
                    rule = searchCss("." + element.attr("class"), sheet);
                    if(rule != null && rule.getStyle().getLength() != 0) result = percentage;   //Check that the css rule isn't empty
                } else if (element.attr("id").length() > 0) {
                    rule = searchCss("#" + element.attr("id"), sheet);
                    if(rule != null && rule.getStyle().getLength() != 0) result = percentage;
                } else {
                    rule = searchCss(divSpan.htmlAttribute, sheet);
                    if(rule != null && rule.getStyle().getLength() != 0) result = percentage;
                }
            }
        }
        return new TestResult(toString(), result, report);
    }

    /***
     * Searches through the css for a rule with the selector specified.
     * @param selector
     * @return The corresponding rule or null if not found
     */
    private CSSStyleRule searchCss(String selector, CSSStyleSheet sheet){
        CSSRuleList ruleList = sheet.getCssRules();

        for (int i = 0; i < ruleList.getLength(); i++) {
            CSSRule rule = ruleList.item(i);
            if (rule instanceof CSSStyleRule) {
                CSSStyleRule styleRule = (CSSStyleRule) rule;
                //Check prefix and whether the check whether rule is empty
                String selectorText = styleRule.getSelectorText();
                if(selectorText.equals(selector)) return styleRule;
            }
        }
        return null;
    }
}
