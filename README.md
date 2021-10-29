# CI4kRPC (Time Based DiscordRPC written in Java)

##### Caught in 4k Rich Presence or CI4kRPC is a discord development project I started in desperation to stop people from dming me while I was at work. This fully configurable DiscordRPC automatically updates to a configured text and images based on the day of the week as well as a fraction of minutes. I hope this RPC will make your life easier in some way as well! This project is also just experience on the side so I can mentally process unix time faster. The time in the configuration must be in 24 hour time. (As of right now times in the am with one digit E.G 04:30 must be typed as 4:30 this will probably be changed in the future. For jar and executable files the config must be in the same directory to work.

### Configuration
```
{
  "client_ID": "client id goes here",
  "default": {
    "state": "",
    "details": "",
    "large_image": "",
    "small_image": "small",
    "large_image_text": "",
    "small_image_text": "opticalPvPX"
  },
  "timings": {
    "monday": {
     "E.G 13:00": {
        "state": "state goes here",
        "details": "details go here",
        "large_image": "large image name goes here",
        "small_image": "small image name goes here",
        "large_image_text": "large image text goes here",
        "small_image_text": "small image text goes here"
      }
    },
    "tuesday": {},
    "wednesday": {},
    "thursday": {},
    "friday": {},
    "saturday": {},
    "sunday": {}
  }
}
