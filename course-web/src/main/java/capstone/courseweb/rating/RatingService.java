package capstone.courseweb.rating;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class RatingService {
    /*
{
    "user_vector": "s2apRu%XvJ#{BX=T&)2=mZAzjYQyV3=0?mu$GHzbJaP{|ngQuPLtF_zYh=$pnaU173MlnH_{kAJ$Jg&ZqkP&vtg6XA$mt3{?(z#iFj(?Fk?HC_0buJsF3AZ$dlUaYg7*4807wKs`qmjgkPPfTS~vbaW=iiqm@)!CsHqA+ga8;nHLC_c&WGPVI@Q}gJ#GX)B7xaH3N!RRRIc$p4(<IuW8ls{9<c#Gerf|hHDMG#+eiOC(1`#)kSGd2fl=%}c+K)Y6c7VHVO0x1&r#w&3G3rNE~w2uKivI3LAVq@YH!j$9+BQY(Do8P^n~X=UwjomjxOdtF5(S8y}%4V96{qgV^9r0ynYQon(nkd4b%od@5<>u&=?UvGEwY45XKfi`-9Ow<3{K{O6=J_spbkl^+WtVtUC8TQ3nJ+mMRE88w2w`E~wHzXrk>tW?BM2XVn2eof-u{61?#~5$x?g<YWFm-EjFnJS7J|kvalDkq!YrWPJ=j1%3fP4sioNjOFM)-)aXxYF`6CS~Ckjs<_iWOw|%UM*{smhR7a2x`XyUdcqAq`@-!$zESc%AafEwG_K4(<MReTFL2d9J8u3yYDV=w`!yjyygcJRtd!9{IBTvxIyCG)<u(pKX0Xmaec;tT0%{XKSkMeVyb1?DIX4?WtgzlbZL$DA9UBEd+)vj&{uTs3#9IwNH7N)`2374orX>A7Uxm*;<j~wcG86tj<EINhqR;m}28aSb_)!Hvj~5L;Gyx4iFd7s;1K$HbPRIv8?pWnM)b#&8c1QL;3z_pi8uueVo6_e#j)n|BgfbXEZY>c%+MoqLNORvl<Ma$ac|7hu?B)7CUV9NgTy_>eH&5(7Tm$7kN$d+hao+zv5cU5)G@K7VnB3PsFk=ZnF@708LT~au^&99ur3&Ca0-p{)$~Nvk^V9M^1jPeC#M90`91Z|KQo;#8^Ck;F%fkOYnFi}VGc5-{=4%%}an$iXAdk{M-R=QD5LX93>EjMR8FLOl=#UpbON|RZhllMxg%t=tI;Z<S6#xW3qqgZjfk_2FKoR#o5@{Dd!dcutH#-wQZG-$iMlT0H5jpHWN(}BkK)nq=-3JOkX5{ofvf=nXgdYJvaAf&DhyMsarT6we5c2;%Rj?307Kg<?2S)%uL+Ii@>X__4JrfT<7Bcca<n8u8dt?beA6oD}4)y^*4HfY|2$BLnqR|XLh)fYb5GV9LOq>EgQ1RqGC}HV7DVYX8S@I)4gi8cJjHU=b`GogAmbMK)3O)%xv1=4RJ|+A<0RFo^iE<%7eJTP!CxHq-nw1Mb4vXtPN@m(V@d^Y#^{fCt^ECcGVY&%FNwo++melS(e_sARWE$T-81xE1raA;at)>G%Y(xM*$Qv3zpq&Um7|{Yh6Q}+@)WYdL7ft>?$8Q5ath@z3<LU}O#6}E1Yhw~WF;4J4uPY2c`u^fRw`%@A@^b_~3mgnTX?PVs=B5chQh)zGk}3&580Zc^aSZ@J<hS=e;6npH-EIIs$?p3;=U($Z6e9^gBh3RpW``C(AuH`Z",
    "total_rating": 20,   // 여지껏 매긴 별점 합
    "count_rating_places": 6,  // 여지껏 별점 매긴 장소 수

    "placeID": 1336760258,  // 현재 별점 매긴 장소
    "rating": 5  // 현재 매긴 별점
 }
     */

    public String sendRatingToFlask(Map<String, Object> ratingRequest) {
        // RestTemplate 사용해 Flask 서버로 POST 요청
        RestTemplate restTemplate = new RestTemplate();
        String flaskUrl = "http://127.0.0.1:5000/rating";  // Flask 서버 주소

        //Flask 서버로 보낼 요청 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(ratingRequest, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(flaskUrl, request, String.class);

        return response.getBody();
    }
}
