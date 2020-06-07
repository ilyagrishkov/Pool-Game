# Request from client to server
This is an overview of all the requests to the server that are made by the client.

## Registration
The way to signup a new user by e-mailaddress.
- Method: **POST**
- URL: **/api/register**

#### JSON Request Body
| Key | Example Value | Description |
| --- | --- | --- |
| username | johndoe74 | The new user's login name. |
| displayName | johndoe42 | The new user's display name. |
| password | my$tr0ngP@ss | The user's desired password.|
    
#### JSON Response Body
| Key | Example Value | Description |
| --- | --- | --- |
| success | true | Whether the registration was succesful |
| errorMessage | Unauthorized | Error message. *Only applicable if `success` equals `false`* |

---

## Authorization
### Login
Sign in an existing user by their username and password.
- Method: **POST**
- URL: **/api/login**
**/api/refresh**

#### JSON Request Body
| Key | Example Value | Description |
| --- | --- | --- |
| username | johndoe74 | The user's login name. |
| password | my$trongP@ss | The user's password. |

#### JSON Response Body
| Key | Example Value | Description |
| --- | --- | --- |
| success | true | Whether the registration was succesful |
| errorMessage | Unauthorized | Error message. *Only applicable if `success` equals `false`* |
| token | hgtyfhbvgh.hgy76ftyug67.juhgy87h9h789 | JWT token |
| refresh_token | 7afs7yuhajwdh7tfsd6tf | JWT token valid for 15 minutes, refresh token used to request new one |

### Refresh
Get a new token using the refresh token
- Method: **POST**
- URL: **/api/refresh**

#### JSON Request Body
| Key | Example Value | Description |
| --- | --- | --- |
| username | johndoe74 | The user's login name. |
| refreshToken | 7afs7yuhajwdh7tfsd6tf | The refresh token. |

#### JSON Response Body
| Key | Example Value | Description |
| --- | --- | --- |
| success | true | Whether the registration was succesful |
| errorMessage | Unauthorized | Error message. *Only applicable if `success` equals `false`* |
| token | hgtyfhbvgh.hgy76ftyug67.juhgy87h9h789 | JWT token |
| refresh_token | 7afs7yuhajwdh7tfsd6tf | JWT token valid for 15 minutes, refresh token used to request new one |
---

## Score
### Submit 
Submit a new score
- Method: **POST**
- URL: **/api/score/submit**

#### JSON Request Body
| Key | Example Value | Description |
| --- | --- | --- |
| displayName | xXx_PoolGuy_xXx | Name which should be associated with the score. |
| score | 42 | The score to submit. |

#### JSON Response Body
| Key | Example Value | Description |
| --- | --- | --- |
| success | true | Whether the registration was succesful |
| errorMessage | Unauthorized | Error message. *Only applicable if `success` equals `false`* |

### List
Get the scores of the user
- Method: **GET**
- URL: **/api/score/list**

#### JSON Request Body
*empty body*

#### JSON Response Body
| Key | Example Value | Description |
| --- | --- | --- |
| success | true | Whether the registration was succesful |
| errorMessage | Unauthorized | Error message. *Only applicable if `success` equals `false`* |
| scores | {[{uuid: blah, score: 42}, {uuid: blah2, score: 8}]} | An array containing all scores of this user. |

### Leaderboard
Retrieve the leaderboard
- Method: **GET**
- URL: **/api/score/leaderboard**

#### JSON Request Body
| Key | Example Value | Description |
| --- | --- | --- |
| offset | 0 | The offset in the leaderboard. |
| entries | 10 | The amount of entries to return. |


#### JSON Response Body
| Key | Example Value | Description |
| --- | --- | --- |
| success | true | Whether the registration was succesful |
| errorMessage | Unauthorized | Error message. *Only applicable if `success` equals `false`* |
| entries | {[{displayName: "yes123", score: 15}, {displayName: "nee32"1, score: 16}]} | An array with leaderboard entries. |