-- this is my take on challenge 3.2 (Craftin Interpreters, by Robert Nystrom)
-- ```The Visitor pattern let's you emulate the functional style in an
--- object-oriented language. Devise a complementary pattern for a functional
-- language. It should let you bundle all of the operations on one type
-- together and let you define new types easily```

-- we define two kind of expressions, additions and negations 
data Expression  = Addition Float Float | Negation Float
-- for simplicity,  we will only have two operations that are performed
-- over the base expressions: 1. `eval`, and 2. `sprint`.

-- the classical, functional approach would implement `eval` and `sprint`
-- by pattern matching over the expression types
eval' :: Expression -> Float
eval' (Addition a b) = a + b
eval' (Negation a) = -a

sprint' :: Expression -> String
sprint' (Addition a b) = show a ++ " + " ++ show b
sprint' (Negation a) = "-" ++ show a

-- here we have  to bundle together all the operations acting on one type,
-- so that adding a new type is manageable.
data Operation = Eval | Print
data Result = FloatResult Float | StringResult String

-- bundlded operations for type `Addition`
additionFs :: Operation -> Float -> Float -> Result
additionFs Eval a b = FloatResult (a + b)
additionFs Print a b = StringResult (show a ++ " + " ++ show b)

-- bundlded operations for type `Addition`
negationFs :: Operation -> Float -> Result
negationFs Eval a = FloatResult (-a)
negationFs Print a = StringResult ("-" ++ show a)

-- don't know if there is a better solution to having these.
-- ideally we would have `Result` to be a generic, but i don't know
-- how to achieve that with haskell.
getFloatResult :: Result -> Float
getFloatResult (FloatResult x) = x
getFloatResult _ = error "wrong result type"

getStringResult :: Result -> String
getStringResult (StringResult x) = x
getStringResult _ = error "wrong result type"

-- now the interface
eval :: Expression -> Float
eval (Addition a b) = getFloatResult $ additionFs Eval a b
eval (Negation a) = getFloatResult $ negationFs Eval a

sprint :: Expression -> String
sprint (Addition a b) = getStringResult $ additionFs Print a b
sprint (Negation a) = getStringResult $ negationFs Print a

-- in this solution, adding a new type would require:
-- 1. to add a new function like these
-- 2. eventually, to add a new type if we do not have a getter already
-- 3. to add a new interface. it should be possible to automatize this
-- quite the mess, however I believe that much of the complication here
-- is due to the fact that apparently we can't easily use a generic
-- for the result type. even if we could, we can see how the solution
-- still requires a indirection layer, as it did in OOP.
-- i'm sure a shorter expression exists but i don't know haskell well
-- enough to find it.
