export floc=~drafiei/291/pub/$1

cat $floc/$1.txt | java parser.DataFileGenerator

echo "Comparing reviews.txt..."
wdiff -3 reviews.txt $floc/reviews.txt
echo "Comparing pterms.txt..."
wdiff -3 pterms.txt $floc/pterms.txt
echo "Comparing scores.txt..."
wdiff -3 scores.txt $floc/scores.txt
echo "Comparing rterms.txt..."
wdiff -3 rterms.txt $floc/rterms.txt
