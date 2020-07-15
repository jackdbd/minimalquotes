(ns minimalquotes.fakes)

(def user-id "q2MbgShXTQgIbKipyGGKK0ZC7Yv2")

(def user {:display-name "John Doe"
           :email "john.doe@emailprovider.com"
           :photo-url "img/minimalquotes-icon.png"
           :uid user-id})

(def quote-id-0 "Hvx2oAFAQ3GWKHUcqRdu")
(def quote-id-1 "ftnfLpbqkBKdWegivYZa")
(def quote-id-2 "MUORu1tzEnqZ75jUUkB6")
(def quote-id-3 "j2zYfWIJ2Yds6pnMUfvo")
(def quote-id-4 "lhhGqYQrNsVb0ZyrV1NW")
(def quote-id-5 "tTZSa7xGoY61lePYs2YX")

(def author-0 "Jeff Atwood")
(def text-0 "You can never have too little minimalism.")

(def quote-0 {:author author-0
              :text text-0})

(def quotes {quote-id-0 quote-0
             quote-id-1 {:author "Mark Twain"
                         :text "Too much of anything is bad, but too much whiskey is just enough."}
             quote-id-2 {:author "Nicolò Machiavelli"
                         :text "Both fortune and virtue are needed to become a prince. Without virtue, no fortune can last; without fortune, your virtues may be useless."}
             quote-id-3 {:author "Zen master"
                         :text "If you walk, just walk. If you sit, just sit; but whatever you do, don't wobble."}
             quote-id-4 {:author "Leon Battista Alberti"
                         :text "No art, however minor, demands less than total dedication if you want to excel in it."}
             quote-id-5 {:author "It doesn’t matter how beautiful your theory is, it doesn’t matter how smart you are. If it doesn’t agree with experiment, it’s wrong."
                         :text "Richard Feynman"}})
