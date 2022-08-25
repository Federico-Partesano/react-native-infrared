/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * Generated with the TypeScript template
 * https://github.com/react-native-community/react-native-template-typescript
 *
 * @format
 */

import React, {useEffect, useState} from 'react';
import {
  SafeAreaView,
  ScrollView,
  StatusBar,
  Text,
  useColorScheme,
  NativeModules,
  Button,
} from 'react-native';

import {Colors} from 'react-native/Libraries/NewAppScreen';

const App = () => {
  const isDarkMode = useColorScheme() === 'dark';
  const [first, setfirst] = useState('');
  const [range, setRange] = useState('');
  const [second, setsecond] = useState('');
  const {Test} = NativeModules;
  useEffect(() => {
    Test.test('asd', (err: any, message: boolean) => {
      setfirst(message ? 'true˚' : 'false');
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const backgroundStyle = {
    backgroundColor: isDarkMode ? Colors.darker : Colors.lighter,
  };

  return (
    <SafeAreaView style={backgroundStyle}>
      <StatusBar barStyle={isDarkMode ? 'light-content' : 'dark-content'} />
      <ScrollView
        contentInsetAdjustmentBehavior="automatic"
        style={backgroundStyle}>
        <Text>{first}</Text>
        <Button
          title="send"
          onPress={() => {
            const SAMSUNG_TURN_ON_PRONTO_CODE = Array.from(
              {length: 256},
              (_, i) => i.toString(16),
            ).join(' ');
            Test.transmitProntoCode(SAMSUNG_TURN_ON_PRONTO_CODE, (_: any) => {
              setsecond(SAMSUNG_TURN_ON_PRONTO_CODE);
            });
          }}
        />
        <Text>{second}</Text>
        <Button
          title="get range"
          onPress={() => {
            Test.range('', (_: any, output: string) => {
              setRange(output);
            });
          }}
        />
        <Text>{'range'}</Text>
        <Text>{range}</Text>
      </ScrollView>
    </SafeAreaView>
  );
};

export default App;
