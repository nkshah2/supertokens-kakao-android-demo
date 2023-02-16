import express from "express";
import supertokens from "supertokens-node";
import Session from "supertokens-node/recipe/session";
import ThirdPartyEmailPassword from "supertokens-node/recipe/thirdpartyemailpassword";
import { middleware } from "supertokens-node/framework/express";
import { errorHandler } from "supertokens-node/framework/express";
import axios from "axios";
let cors = require("cors");
import cookies from "cookie-parser";
import morgan from "morgan";

const apiPort = 3001;
const apiDomain = "http://localhost:" + apiPort;

supertokens.init({
    framework: "express",
    supertokens: {
        // try.supertokens.io is for demo purposes. Replace this with the address of your core instance (sign up on supertokens.io), or self host a core.
        connectionURI: "https://try.supertokens.io"
        // apiKey: "IF YOU HAVE AN API KEY FOR THE CORE, ADD IT HERE",
    },
    appInfo: {
        // learn more about this on https://supertokens.io/docs/thirdpartyemailpassword/appinfo
        appName: "Demo App",
        apiDomain: apiDomain,
        websiteDomain: "http://localhost:3000"
    },
    recipeList: [
        ThirdPartyEmailPassword.init({
            providers: [
                {
                    id: "kakao",
                    get: (redirectURI, authCodeFromRequest) => {
                        return {
                            accessTokenAPI: {
                                // this contains info about the token endpoint which exchanges the auth code with the access token and profile info.
                                url: "https://kauth.kakao.com/oauth/token",
                                params: {
                                    // example post params
                                    client_id: "0738d92ab58d6d7d76498f76ad586028",
                                    client_secret: "tPPFOW8EKvPu2skhvEOAmLbZoXsOtFPQ",
                                    grant_type: "authorization_code",
                                    redirect_uri: redirectURI || "",
                                    code: authCodeFromRequest || "",
                                    //...
                                }
                            },
                            authorisationRedirect: {
                                url: "https://kauth.kakao.com/oauth/authorize",
                                params: {
                                    client_id: "0738d92ab58d6d7d76498f76ad586028",
                                    response_type: "code",
                                }
                            },
                            getClientId: () => {
                                return "0738d92ab58d6d7d76498f76ad586028";
                            },
                            getProfileInfo: async (accessTokenAPIResponse) => {
                                const response = await axios.get("https://kapi.kakao.com/v2/user/me", {
                                    headers: {
                                        "Authorization": `Bearer ${accessTokenAPIResponse.access_token}`,
                                        "Content-Type": "application/x-www-form-urlencoded;charset=utf-8"
                                    },
                                });

                                return {
                                    id: `${response.data.id}`,
                                    email: {
                                        id: response.data.kakao_account.email,
                                        isVerified: response.data.kakao_account.is_email_verified,
                                    },
                                };
                            }
                        }
                    }
                }
            ]
        }),
        Session.init() // initializes session features
    ]
});

let app = express();

app.use(cookies())
app.use(morgan("[:date[iso]] :url :method :status :response-time ms - :res[content-length]"));

app.use(
    cors({
        origin: "http://localhost:3000",
        allowedHeaders: ["content-type", ...supertokens.getAllCORSHeaders()],
        credentials: true
    })
);

app.use(middleware());
app.use(express.json());

app.use(errorHandler());

app.listen(apiPort, () => { console.log("Server started") });