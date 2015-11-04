from bsddb3 import db
#Get an instance of BerkeleyDB
DATABASE='fruit_example.db'
# Create a new databaseof type HASH 
database2 = db.DB()
database2.open(DATABASE, None, db.DB_HASH, db.DB_CREATE)
#Get cursor object
cur = database2.cursor()
#(key,data) pair
cur.put(b'apple', "red",db.DB_KEYFIRST)
database2.put(b'pear', "green")
# get all rows inserted into the database
iter = cur.first()
while iter:
 print(iter)
 iter = cur.next()
print("------------------------")
#get only a specific row
result = cur.get(b'apple', db.DB_FIRST)
print(result)
#Will give (b'apple', b'red')
# get only the data at that key
result = database2.get(b'pear')
#Will give b'test2'
print(result)
# removing from the database
# using database object
database2.delete(b'apple')
iter = cur.first()
while iter:
 print(iter)
 iter = cur.next()
print("------------------------")
# using cursor
cur.put(b'zat', "xat",db.DB_KEYFIRST)
iter = cur.first()
while iter:
 print(iter)
 iter = cur.next()
print("------------------------")
cur.delete() # deletes the key-data pair currently referenced by the cursor
iter = cur.first()
while iter:
 print(iter)
 iter = cur.next()
print("------------------------")

cur.close()
database2.close()

##
#(b'apple', b'red')
#(b'pear', b'green')
#------------------------
#(b'apple', b'red')
#b'green'
#(b'pear', b'green')
#------------------------
#(b'pear', b'green')
#(b'zat', b'xat')
#------------------------
#(b'pear', b'green')
#------------------------
#
