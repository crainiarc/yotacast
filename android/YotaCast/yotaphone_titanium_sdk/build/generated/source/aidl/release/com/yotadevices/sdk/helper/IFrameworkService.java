/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/khankuan/Dropbox/Code/Android/YodaCast/yotaphone_titanium_sdk/src/com/yotadevices/sdk/helper/IFrameworkService.aidl
 */
package com.yotadevices.sdk.helper;
public interface IFrameworkService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.yotadevices.sdk.helper.IFrameworkService
{
private static final java.lang.String DESCRIPTOR = "com.yotadevices.sdk.helper.IFrameworkService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.yotadevices.sdk.helper.IFrameworkService interface,
 * generating a proxy if needed.
 */
public static com.yotadevices.sdk.helper.IFrameworkService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.yotadevices.sdk.helper.IFrameworkService))) {
return ((com.yotadevices.sdk.helper.IFrameworkService)iin);
}
return new com.yotadevices.sdk.helper.IFrameworkService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_goToSleep:
{
data.enforceInterface(DESCRIPTOR);
this.goToSleep();
reply.writeNoException();
return true;
}
case TRANSACTION_wakeUp:
{
data.enforceInterface(DESCRIPTOR);
this.wakeUp();
reply.writeNoException();
return true;
}
case TRANSACTION_lockOn:
{
data.enforceInterface(DESCRIPTOR);
this.lockOn();
reply.writeNoException();
return true;
}
case TRANSACTION_lockOff:
{
data.enforceInterface(DESCRIPTOR);
this.lockOff();
reply.writeNoException();
return true;
}
case TRANSACTION_lockBackScreen:
{
data.enforceInterface(DESCRIPTOR);
this.lockBackScreen();
reply.writeNoException();
return true;
}
case TRANSACTION_unlockBackScreen:
{
data.enforceInterface(DESCRIPTOR);
this.unlockBackScreen();
reply.writeNoException();
return true;
}
case TRANSACTION_isLockScreenDisabled:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isLockScreenDisabled();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_performSingleUpdate:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.performSingleUpdate(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.yotadevices.sdk.helper.IFrameworkService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void goToSleep() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_goToSleep, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void wakeUp() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_wakeUp, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void lockOn() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_lockOn, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void lockOff() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_lockOff, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void lockBackScreen() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_lockBackScreen, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void unlockBackScreen() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_unlockBackScreen, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public boolean isLockScreenDisabled() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isLockScreenDisabled, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void performSingleUpdate(int waveform) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(waveform);
mRemote.transact(Stub.TRANSACTION_performSingleUpdate, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_goToSleep = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_wakeUp = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_lockOn = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_lockOff = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_lockBackScreen = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_unlockBackScreen = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_isLockScreenDisabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_performSingleUpdate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
}
public void goToSleep() throws android.os.RemoteException;
public void wakeUp() throws android.os.RemoteException;
public void lockOn() throws android.os.RemoteException;
public void lockOff() throws android.os.RemoteException;
public void lockBackScreen() throws android.os.RemoteException;
public void unlockBackScreen() throws android.os.RemoteException;
public boolean isLockScreenDisabled() throws android.os.RemoteException;
public void performSingleUpdate(int waveform) throws android.os.RemoteException;
}
