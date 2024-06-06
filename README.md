# Yelp Clone 

## *Emmanuel Gyabaah*

**Yelp clone** displays a list of search results from the Yelp API and displays the results in a scrollable list. 

Time spent: **25** hours spent in total

## Functionality 

The following **required** functionality is completed:

* [x] Ability to query the Yelp API to get results from a search query
* [x] The search results are displayed in a RecyclerView

The following **extensions** are implemented:

* [x] User sees a descriptive error message if internet is not available
* [x] Add a search component in the action bar so the user can query for another item instead of the one hardcoded in the app.
* [x] (Advanced) Add a menu option to add filters to the search.

## Video Walkthrough

Here's a walkthrough of implemented user stories:

<img src='http://i.imgur.com/qXPywKm.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' />

GIF created with [OBS 30.1.2 & EZGif](https://ezgif.com/).

## Notes

Describe any challenges encountered while building the app.
* I struggled a bit with implementing the price filter since yelp API expect each price number to be a seperate param and Users might not include all price numbers in their query. I was able to resolve this issue after finding out from retrofit documentation that @Query tag will not be added as a query parameter if it is null.

## License

    Copyright 2024 Emmanuel Gyabaah

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
